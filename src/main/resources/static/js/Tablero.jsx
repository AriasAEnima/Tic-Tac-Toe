// Retorna la url del servicio. Es una función de configuración.

function BBServiceURL() {
    var chost=window.location.host;
    var nprotocol="";
    if(window.location.protocol=='http:'){
        nprotocol="ws";
    }else{
        nprotocol="wss";
    }
    console.log(window.location.protocol); 
    console.log(chost); 
    return nprotocol+'://'+chost+'/TableroService';
}
class WSBBChannel {
    constructor(URL, callback,partida) {
        this.URL = URL;
        this.wsocket = new WebSocket(URL);
        this.wsocket.onopen = (evt) => this.onOpen(evt);
        this.wsocket.onmessage = (evt) => this.onMessage(evt);
        this.wsocket.onerror = (evt) => this.onError(evt);
        this.receivef = callback;
        this.partida=partida;
    }
    onOpen(evt) {
        console.log("In onOpen", evt);
        this.send(this.partida,null, null);
    }
    onMessage(evt) {
        console.log("In onMessage", evt);
        // Este if permite que el primer mensaje del servidor no se tenga en        cuenta.
                // El primer mensaje solo confirma que se estableció la conexión.
                // De ahí en adelante intercambiaremos solo puntos(x,y) con el servidor
        if(evt.data != "Connection established.")
        {
            this.receivef(evt.data);
        }
    }

    onError(evt) {
        console.error("In onError", evt);
    }
    send(id,f, s) {
        let msg = '{ "id": "'+ (id) +'" ,"f": ' + (f) + ', "s": "' + (s) + '"}';
        console.log("sending: ", msg);
        this.wsocket.send(msg);
    }   
    sendRestart(id,f,s){
        let msg = '{ "id": "'+ (id) +'" ,"f": ' + (f) + ', "s": "' + (s) + '" , "reiniciar": true}';
        console.log("sending: ", msg);
        this.wsocket.send(msg);
    }
}



class Tablero extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            error: null,
            isLoaded: true,
            fichas: ["","","","","","","","",""],
            signo: "",
            partida: "",
            enPartida: false
        };     
            
        this.actualizarPartida = this.actualizarPartida.bind(this);      
        this.entrarPartida = this.entrarPartida.bind(this);      
    }    
    
    actualizarPartida(event) {
        this.setState({ partida: event.target.value });
    }
    
    entrarPartida() { 
        if(this.state.partida === "" ){         
             this.setState({ msg: "Campos no valido" });
        } else{        
            const data = new FormData();       
            data.append('id', this.state.partida);                      
            fetch('/partida', {
                method: 'POST',
                body: data
            })
                    .then(response=> response.json())
                    .then(dataresponse=>{                        
                        this.setState({ enPartida: true , fichas: dataresponse.fichas}); 
                        this.comunicationWS = new WSBBChannel(BBServiceURL(),
                                (msg) => {
                            console.log("On func call back ", msg);
                            var obj = JSON.parse(msg);
                            
                            if (obj.f != undefined) {
                                this.retomarficha(obj.f, obj.s);
                            } else if (obj.reiniciar!=undefined){
                                console.log("llego un reinicio");
                                this.setState({
                                    fichas: ["","","","","","","","",""]
                                });
                            }else {
                                this.setState({
                                    signo: obj.s
                                });
                            }
                        },this.state.partida);                        
                    });        
        }        
    }  
    
    traerEstadoServidor(){               
        const data = new FormData();       
        data.append('id', this.state.partida);                      
        fetch('/partida', {
            method: 'POST',
            body: data
        })
                .then(response=> response.json())
                .then(dataresponse=>{                        
                    this.setState({ enPartida: true , fichas: dataresponse.fichas});                                  
                });                 
        
    }
    
    actualizarficha(f){
        var fichast=this.state.fichas;
        if (fichast[f]==""){
            fichast[f]=this.state.signo;
            this.setState({                              
                                fichas: fichast
                            });
            let wsreference = this.comunicationWS;
            wsreference.send(this.state.partida,f,this.state.signo);    
        }         
        
    }
    
    
    retomarficha(f,s){
        var fichast=this.state.fichas;
        fichast[f]=s;
        this.setState({ fichas: fichast  });                             
                          
    }
    

    
    reiniciar() {
        this.setState({
            fichas: ["", "", "", "", "", "", "", "", ""]
        });       
        let wsreference = this.comunicationWS;
        wsreference.sendRestart(this.state.partida,null,this.state.signo);   
    } 
    
    guardar(){
        const data = new FormData();
        data.append('id', this.state.partida);
        data.append('fichas',this.state.fichas);
        fetch('/guardar', {
            method: 'POST',
            body: data
        }).then(function (response) {
            if (response.ok) {
                return response.text();
            } else {
                throw "Error en la llamada Ajax";
            }
        });        
    }
    
    render() {
        const {error, isLoaded, status,enPartida,partida} = this.state;
        if (error) {
            return <div>Error: {error.message}</div>;
        } else if (!isLoaded) {
            return <div>Loading...</div>;
            
        } else if (!enPartida){
            return (
                   <div id="formularioLogin">      
                       <p>
                           <label htmlFor ="npartida">Ingrese Sala : </label>
                           <input id="npartida" type="text" onChange={this.actualizarPartida}/>
                       </p>         
                        <p>
                         <button type="button" onClick={this.entrarPartida}>Entrar ! </button>                   
                        </p>
                    </div>
                    );
        } else {
            return (                    
              
                    <div id="tablero">
                        <div className="ficha" onClick={() => this.actualizarficha('0')}>
                            {this.state.fichas[0]}        
                        </div>
                        <div className="ficha" onClick={() => this.actualizarficha('1')}>
                            {this.state.fichas[1]}        
                        </div>

                        <div className="ficha" onClick={() => this.actualizarficha('2')}>
                            {this.state.fichas[2]}        
                        </div>

                        <div className="ficha" onClick={() => this.actualizarficha('3')}>
                            {this.state.fichas[3]}        
                        </div>
                        <div className="ficha" onClick={() => this.actualizarficha('4')}>
                            {this.state.fichas[4]}        
                        </div>

                        <div className="ficha" onClick={() => this.actualizarficha('5')}>
                            {this.state.fichas[5]}        
                        </div>

                        <div className="ficha" onClick={() => this.actualizarficha('6')}>
                            {this.state.fichas[6]}        
                        </div>

                        <div className="ficha" onClick={() => this.actualizarficha('7')}>
                            {this.state.fichas[7]}        
                        </div>

                        <div className="ficha" onClick={() => this.actualizarficha('8')}>
                            {this.state.fichas[8]}        
                        </div>
                        <button onClick={()=> this.reiniciar()}> Reinicie! </button>  
                        <button onClick={()=> this.guardar()}> Guardar! </button>  
                        <button onClick={()=> this.traerEstadoServidor()}> Ultimo Estado Guardado! </button>
                                
            

                  

                    </div>                       
                    
                    );
        }
    }
}

ReactDOM.render(
        <Tablero />,
        document.getElementById('contenedortablero')
);
var app_key = '9cc0bc2e04c995ae545e';
var canal = 'chat';
var evento = 'mensaje';
var usuario;

$(document).ready(function() {
  //Verificamos que ya se haya escogido un nombre de ususario
    if(!localStorage.getItem('usuario')){
      while(trim(usuario).length == 0){
        usuario = prompt("Nombre:");
        localStorage.setItem('usuario',usuario);
      }
    }
    else{
        usuario = localStorage.getItem('usuario');
    }

   var titleOriginal = document.title;   
   var intervalBlinkTitle = 0;
   var primero = true;
    // Funcion que hace que parpadee el titulo de la pagina cada que llega un mensage nuevo
   window.startFlashTitle = function (newTitle) {       
       if(intervalBlinkTitle == 0){
            document.title = 
             (document.title == titleOriginal) ? newTitle : titleOriginal;           

            intervalBlinkTitle = setInterval(function (){
             document.title = 
              (document.title == titleOriginal) ? newTitle : titleOriginal;           
            }, 1000);
        } 
   };

   //Restablece el titulo de la pagina y deja de parpadear
   window.stopFlashTitle = function () {       
       clearInterval(intervalBlinkTitle);
       intervalBlinkTitle = 0;
       document.title = titleOriginal;
   };

   //Al dar un click sobre la ventana nos dejara de mostrar el titulo
   $(window).click(function(event) {
      stopFlashTitle();
   });


   //Si damos enter al cuadro de texto...mandamos el mesaje que escribimos
   $('#msg').keyup(function(event) {
        event.preventDefault();
        if(event.keyCode == 13){
            enviarMensaje($('#msg').val(),false);
            $('#msg').val('');
        }
   });

   //Si apretamos el boton enviar enviamos el mensaje que escribimos
   $('#bt-enviar').click(function(event) {
       enviarMensaje($('#msg').val(),false);
       $('#msg').val('');
   });

   //Cuando hacemos click en una carita formamos el mensaje a enviar
    $('.emo').click(function(event) {
        msg = '<img src="'+this.src+'"width="40" height="40">';
        enviarMensaje(msg,true);
    });


    //Creamos un onjeto Pusher que recibe como parametro la KEY
    var pusher = new Pusher(app_key);
    //Suscribirnos a un canal de comunicacion...en este caso llamado chat
    var channel = pusher.subscribe(canal);
    //Escuchamos un evento...en este caso llamado mensaje... cuando escuche por el evento entonces mostrara el mensaje recibido
    channel.bind(evento, function(data) {
        //Sacamos la fecha del mensaje
        fecha = new Date();
        //de la fecha tomamos nadamas la hora
        hora = fecha.toString().split(' ');
        //Formamos nuestro mensaje 
        cad = '<li class="left clearfix"><span class="chat-img pull-left">'+
              '<img src="http://placehold.it/50/FA6F57/fff&text=CHAT" alt="User Avatar" class="img-circle" />'+
                '</span>'+
                '<div class="chat-body clearfix">'+
                '<div class="header">'+
                '<strong class="primary-font">'+data.usuario+'</strong> <small class="pull-right text-muted">'+
                '<span class="glyphicon glyphicon-time"></span>'+hora[4]+'</small>'+
                '</div>'+
                '<p>'+data.mensaje+
                '</p>'+
                '</div>'+
                '</li>';

        //Agregamos el mensaje al div
        $('#mensajes').append(cad);
        //Hacemos un scroll automatico...para ver el ultimo mensake
        $("#divmsg").scrollTop($("#mensajes")[0].scrollHeight+50);
        //Si nostros enviamos el mensaje no no hacemos que el titulo parpadee
        if(usuario != data.usuario)startFlashTitle('Nuevo Mensaje');
    });

    

});


/*Esta funcion envia un mensaje al presionar "Enviar Mensaje", primero comprueba si ya existe un nombre de usuario
si no, entonces crea uno (id unico),despues se usa la funcion $.get de jquery para enviarle el mensaje al server*/

function enviarMensaje(msg,emoti){
    //comprovamos que el mensaje no este vacio...si es haci no enviamos nada
    if(trim(msg).length == 0)return;
   
    //enviamos el mensaje al servidor mediante AJAX
    $.post('server.php',{usuario:usuario,mensaje:msg,emoticone:emoti},function(data) {
    });
}

//Funcion que quita los espacios de una cadena
function trim(cadena){
       if(typeof(cadena) == 'undefined' || cadena == null)return "";
       cadena=cadena.replace(/^\s+/,'').replace(/\s+$/,'');
       return(cadena);
}

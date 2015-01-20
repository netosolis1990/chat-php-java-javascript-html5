<?php
	$app_key = "9cc0bc2e04c995ae545e";
	$app_secret = "f4d839f39fee5a80bd92";
	$app_id = "95063";
	$canal = "chat";
	$evento = "mensaje";

	//importamos la libreria de pusher
	require('libs/Pusher.php');
	//comprobamos que llego un mensaje por el metodo GET
	if($_POST){
		if(isset($_POST['usuario']) && isset($_POST['mensaje']) && isset($_POST['emoticone'])){
			//Checamos que los mensages no contengan codigo html ni php a menos que sean emoticones...
			//Esto es para evitar ataques
			$usuario = strip_tags($_POST['usuario']);
			if($_POST['emoticone'] == "false"){
			 	$mensaje = strip_tags($_POST['mensaje']);
			}
			else $mensaje = $_POST['mensaje'];

			/*creamos un objeto pusher que recibe como parametros APP_KEY, APP_SECRET, APP_ID */
			$pusher = new Pusher($app_key, $app_secret, $app_id);
			$pusher->trigger($canal, $evento, array('usuario'=> $usuario,'mensaje' => ($mensaje) ));
			echo json_encode($_POST);
		}
	}
?>
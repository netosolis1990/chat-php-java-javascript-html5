package pusher;
/**
 *
 * @author NetoSolis-
 */
import com.pusher.client.*;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionStateChange;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JEditorPane;
import com.google.gson.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLEncoder;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLEditorKit;

public class Pusher implements ChannelEventListener, ConnectionEventListener{
    
    private final String app_key = "9cc0bc2e04c995ae545e";
    private final String canal = "chat";
    private final String evento = "mensaje";
    private final String host = "http://demos.netosolis.com/chatphpjava/server.php";
    
    private final com.pusher.client.Pusher pusher;
    private final long startTime = System.currentTimeMillis();
    private String mensajes = "";
    private JEditorPane panel;
    
    
    public Pusher(JEditorPane p){
        panel = p;
        PusherOptions options = new PusherOptions().setEncrypted(true);
        pusher = new com.pusher.client.Pusher(app_key, options);
        pusher.connect(this);
        pusher.subscribe(canal, this,evento );
    }
    
    @Override
    public void onConnectionStateChange(ConnectionStateChange change) {

        System.out.println(String.format(
                "[%d] Connection state changed from [%s] to [%s]",
                timestamp(), change.getPreviousState(), change.getCurrentState()));
    }

    @Override
    public void onError(String message, String code, Exception e) {

        System.out.println(String.format(
                "[%d] An error was received with message [%s], code [%s], exception [%s]",
                timestamp(), message, code, e));
    }
    
    @Override
    public void onSubscriptionSucceeded(String channelName) {

        System.out.println(String.format(
                "[%d] Subscription to channel [%s] succeeded",
                timestamp(), channelName));
    }
    
    //Funcion que se activa cuando llega un mensaje del servidor, es decir cuando alguien envia un mensaje
    @Override
    public void onEvent(String channelName, String eventName, String data) {
         System.out.println(String.format(
                "[%d] Received event [%s] on channel [%s] with data [%s]",
                timestamp(), eventName, channelName, data));
         //Se decodifica el JSON en la clase Mensaje y agrega al panel para poder verlo
         Gson datos = new Gson();
         Mensaje mensaje =  datos.fromJson(data, Mensaje.class);
         mensajes = "<p><b>"+mensaje.usuario+": </b><br>"+mensaje.mensaje+"<hr noshade>";
         append(mensajes);    
    }
    
    //Funcion sirve para ir agregando los mensages recibidos al panel
    public void append(String s) {
        try {
            HTMLEditorKit kit =(HTMLEditorKit) panel.getEditorKitForContentType("text/html");
            StringReader reader = new StringReader(s);
            kit.read(reader, panel.getDocument(), panel.getDocument().getLength());
            panel.setCaretPosition(panel.getDocument().getLength());
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
    }
     
    //Funcion para enviar un mensaje al servidor
    public void enviaMensaje(String usuario, String mensaje, String emoticone){
        try{
            String params =
                    "usuario=" + URLEncoder.encode(usuario, "UTF-8") +
                    "&mensaje=" + URLEncoder.encode(mensaje, "UTF-8") +
                    "&emoticone=" + URLEncoder.encode(emoticone, "UTF-8");
            
            String res = Pusher.excutePost(host, params);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    private long timestamp() {
        return System.currentTimeMillis() - startTime;
    }
    
    public static String excutePost(String targetURL, String urlParameters)
  {
    URL url;
    HttpURLConnection connection = null;  
    try {
      //Crear la conexion
      url = new URL(targetURL);
      connection = (HttpURLConnection)url.openConnection();
      connection.setRequestMethod("POST");
      connection.setRequestProperty("Content-Type", 
           "application/x-www-form-urlencoded");
			
      connection.setRequestProperty("Content-Length", "" + 
               Integer.toString(urlParameters.getBytes().length));
      connection.setRequestProperty("Content-Language", "en-US");  
			
      connection.setUseCaches (false);
      connection.setDoInput(true);
      connection.setDoOutput(true);

      //Enviar Peticion
      DataOutputStream wr = new DataOutputStream (
                  connection.getOutputStream ());
      wr.writeBytes (urlParameters);
      wr.flush ();
      wr.close ();

      //Tomar Respuesta	
      InputStream is = connection.getInputStream();
      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
      String line;
      StringBuffer response = new StringBuffer(); 
      while((line = rd.readLine()) != null) {
        response.append(line);
        response.append('\r');
      }
      rd.close();
      return response.toString();

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {

      if(connection != null) {
        connection.disconnect(); 
      }
    }
  }
    
    //Clase para manejar el JSON que devuleve pusher cuando llega un mensaje
    public class Mensaje{
        private String usuario;
        private String mensaje;
        private String emoticone;

        public Mensaje(String usuario, String mensaje, String emoticone) {
            this.usuario = usuario;
            this.mensaje = mensaje;
            this.emoticone = emoticone;
        }

        public String getUsuario() {
            return usuario;
        }

        public String getMensaje() {
            return mensaje;
        }

        public String getEmoticone() {
            return emoticone;
        }

        public void setUsuario(String usuario) {
            this.usuario = usuario;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

        public void setEmoticone(String emoticone) {
            this.emoticone = emoticone;
        }
        
    }
    
    
}

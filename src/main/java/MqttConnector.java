
import com.fasterxml.jackson.databind.ObjectMapper;
import models.FindResultModel;
import models.ObservationReq;
import models.Thing;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.atlas.gateway.components.wsn.messages.WSNMessage;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.apache.http.HttpHeaders.USER_AGENT;

public class MqttConnector implements MqttCallback{

    private static final Logger logger = LoggerFactory.getLogger(MqttConnector.class);
    private MqttClient mqclient;
    private Integer rssi = 0;
    private Integer tvRssi = 0;
    private Integer bathroomRssi = 0;
    private Utils.Location location= Utils.Location.LOCATION1;
    private Utils.Location currentLocation = Utils.Location.LOCATION1;
    private static final Map<String, Long> thingIds;
    private ObjectMapper mapper = new ObjectMapper();

    static
    {
        thingIds = new HashMap<String, Long>();
        thingIds.put("00:02:5B:00:B9:10", new Long(26));
        thingIds.put("00:02:5B:00:B9:12", new Long(24));
        thingIds.put("00:02:5B:00:B9:16", new Long(25));
    }

    public MqttConnector(boolean connect){
        if( !connect ){
            logger.info("MQTT Connection disabled by user");
            return;
        }
        try {
//            this.mqclient = new MqttClient("tcp://172.21.13.170:1883", "relative-localization-agent-impl", new MemoryPersistence());
            this.mqclient = new MqttClient("tcp://150.140.187.125:1883", "relativeLoc", new MemoryPersistence());
            this.mqclient.setCallback(this);
            this.mqclient.connect(this.getConnectioOptions());
            logger.info("Successfully Connected to main gateway");
            this.mqclient.subscribe("wsn/ble/devices/advertisments");
            logger.info("Suscribed to topic get advertisments: wsn/ble/devices/advertisments");
        } catch (MqttException e) {
            logger.error("Error while trying to connect to MQTT provide",e);
        }
    }

    private MqttConnectOptions getConnectioOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName("application");
        options.setPassword("@PpL3c@tI0n".toCharArray());
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        return options;
    }

    public MqttClient getClient(){
        return this.mqclient;
    }


    public void connectionLost(Throwable cause) {}


    public void messageArrived(String topic, MqttMessage message) throws Exception {
        WSNMessage.Advertisment advertisment = WSNMessage.Advertisment.parseFrom(message.getPayload());

        logger.info(advertisment.getAddress());
        publishMsg();

        if( advertisment.getAddress().equals("00:02:5B:00:B9:10") || advertisment.getAddress().equals("00:02:5B:00:B9:12") ){

            if ((advertisment.getData().toByteArray()[27]& 0xFF) > 126){
                rssi = (advertisment.getData().toByteArray()[27]& 0xFF) - 256;
            }
            else {
                rssi = - (advertisment.getData().toByteArray()[27]& 0xFF);
            }


            if (advertisment.getAddress().equals("00:02:5B:00:B9:10")){
                tvRssi = rssi;
            }
            if (advertisment.getAddress().equals("00:02:5B:00:B9:12")){
                bathroomRssi = rssi;
            }

            logger.info("10: " + tvRssi + " --- " + "12: " + bathroomRssi);

            if (tvRssi > bathroomRssi){
                currentLocation = Utils.Location.LOCATION1;
//                logger.info("TV");
            }
            else if (tvRssi < bathroomRssi){
                currentLocation = Utils.Location.LOCATION2;
//                logger.info("BATHROOM");
            }

            if (location != currentLocation){
                location = currentLocation;
                logger.debug(location.name());

                ObservationReq observationReq = new ObservationReq();
                Thing thing = new Thing();
                observationReq.setId(28);
                observationReq.setName(location.name());
                observationReq.setTimestamp(new Date().getTime());
                thing.setName("Thing");
                thing.setSensorId(thingIds.get(advertisment.getAddress()));
                thing.setId(29);
                List<Thing> things = new ArrayList<>();
                things.add(thing);
                observationReq.setThings(things);

                FindResultModel frm = new FindResultModel();
                frm.setName("thing");
                frm.setPlace(location.name());
                frm.setDate(new Date());
                logger.debug(frm.toString());

/*                HttpClient client = HttpClientBuilder.create().build();
                HttpPost post = new HttpPost("http://localhost:8080/find/addresult");
                // add header
                post.setHeader("User-Agent", USER_AGENT);
                StringEntity params =new StringEntity("{\"name\":\"" + "thing" +"\",\"location\":\"" + location.name() + "\",\"date\":\"" + new Date().getTime() + "\")} ");
                post.addHeader("content-type", "application/json");
                post.setEntity(params);
                HttpResponse response = client.execute(post);
                System.out.println("Response Code : "
                        + response.getStatusLine().getStatusCode());*/


                byte[] tmp = mapper.writeValueAsString(observationReq).getBytes();

//                tvRssi = -1000;
//                bathroomRssi = -1000;
//                this.mqclient.publish("twg/relative/room_status_mqtt",new MqttMessage(tmp));
//                this.publishMsg();
            }
        }
    }

    private void publishMsg(){
        String msg = "{\"uuid\":\"b1252fb0-ada3-4617-9bd6-6af0addf9c1d\",\"timestamp\":1494003326102,\"device\":\"B0:B4:48:C9:26:01\",\"datatype\":\"temperature\",\"value\":26.91,\"payload\":\"Chair,12.4,0,0.6\"}";
        try {
            if(this.mqclient.isConnected())
              this.mqclient.publish("apps/notifications",new MqttMessage(msg.getBytes()));
            else
                System.out.println("-------");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}


/**
 * 12 -> 24, 16 -> 25, 10 -> 26
 */
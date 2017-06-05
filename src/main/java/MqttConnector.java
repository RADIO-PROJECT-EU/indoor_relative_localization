
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.InvalidProtocolBufferException;
import models.FindResultModel;
import models.Location;
import models.ObservationReq;
import models.Thing;
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
import java.util.Date;

public class MqttConnector implements MqttCallback{

    private static final Logger logger = LoggerFactory.getLogger(MqttConnector.class);
    private MqttClient mqclient;
    private int rssi = 0;
    private int tvRssi = 0;
    private int bathroomRssi = 0;
    private Utils.Location location= Utils.Location.LOCATION1;
    private Utils.Location currentLocation = Utils.Location.LOCATION1;
    private static final Map<String, Long> thingIds;
    private ObjectMapper mapper = new ObjectMapper();
    private Location locationObject;

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

        boolean isconnected =false;

        while(!isconnected){
            try {
//            this.mqclient = new MqttClient("tcp://172.21.13.170:1883", "relative-localization-agent-impl", new MemoryPersistence());
//                this.mqclient = new MqttClient("tcp://10.10.20.110:1883", "relativeLoc", new MemoryPersistence());
                this.mqclient = new MqttClient("tcp://192.168.0.156:1883", "relativeLoc", new MemoryPersistence());
                this.mqclient.setCallback(this);
                this.mqclient.connect(this.getConnectioOptions());
                isconnected = this.mqclient.isConnected();

                if (isconnected){
                    logger.info("Successfully Connected to main gateway");
                    this.mqclient.subscribe("wsn/ble/devices/advertisments");
                    logger.info("Suscribed to topic get advertisments: wsn/ble/devices/advertisments");
                }


            } catch (MqttException e) {
                logger.error("Error while trying to connect to MQTT provide",e);
                isconnected = false;
            }

            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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


    public void messageArrived(String topic, MqttMessage message) throws InvalidProtocolBufferException, JsonProcessingException {
        WSNMessage.Advertisment advertisment = WSNMessage.Advertisment.parseFrom(message.getPayload());

        if( advertisment != null && advertisment.getAddress() != null &&
                (advertisment.getAddress().equals("00:02:5B:00:B9:10") ||
                        advertisment.getAddress().equals("00:02:5B:00:B9:12") )){

            if ((advertisment.getData().toByteArray()[27] & 0xFF) > 126) {
                rssi = (advertisment.getData().toByteArray()[27] & 0xFF) - 256;
            } else {
                rssi = -(advertisment.getData().toByteArray()[27] & 0xFF);
            }


            if (advertisment.getAddress().equals("00:02:5B:00:B9:10")){
                tvRssi = rssi;
            }
            if (advertisment.getAddress().equals("00:02:5B:00:B9:12")){
                bathroomRssi = rssi;
            }

            logger.debug("10: " + tvRssi + " --- " + "12: " + bathroomRssi);


            if (tvRssi > bathroomRssi){
                currentLocation = Utils.Location.LOCATION1;
//                publishMsg(Room.TV);
            }
            else if (tvRssi < bathroomRssi){
                currentLocation = Utils.Location.LOCATION2;
//                publishMsg(Room.BATH);
            }
            else {
                return;
            }

            if (location != currentLocation){
                location = currentLocation;
                logger.debug(location.name());
                publishMsg(location.toString());

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
                logger.info(frm.toString());

                byte[] tmp = mapper.writeValueAsString(observationReq).getBytes();
            }
        }
    }

    private void publishMsg(String location) throws JsonProcessingException {
        String msg = msg = "{\"uuid\":\"b1252fb0-ada3-4617-9bd6-6af0addf9c1d\",\"timestamp\":1494003326102,\"device\":\"B0:B4:48:C9:26:01\",\"datatype\":\"temperature\",\"value\":26.91,\"payload\":\"Chair,10.83,0,1.1\"}";
            locationObject = new Location();
            locationObject.setLocation(location);
            locationObject.setCreatedAt(new Date());
            locationObject.setObject("thing");
            msg = mapper.writeValueAsString(locationObject);
        try {
            if(this.mqclient.isConnected())
                this.mqclient.publish("apps/localization/relative",new MqttMessage(msg.getBytes()));
            else
                System.out.println("-------");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public void deliveryComplete(IMqttDeliveryToken token) {

    }

    public enum Room {
        TV,
        BATH
    }

}

/*
    Με Chair,12.4,0,0.6 τοποθετείται κοντά στην TV.
    Με Chair,6.6,0,1 τοποθετείται μέσα στο μπάνιο.
    Με Chair,10.83,0,1.1 τοποθετείται στην αρχική της θέση στο τραπέζι.
*/


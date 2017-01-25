package emcity.luci;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esotericsoftware.minlog.Log;

import emcity.Cluster;
import emcity.EmCity;
import emcity.Reader;
import luci.connect.AttachmentAsArray;
import luci.connect.JSON;
import luci.connect.LcRemoteService;
import luci.connect.Message;
import peasy.PeasyCam;
import peasy.org.apache.commons.math.geometry.Rotation;
import peasy.org.apache.commons.math.geometry.Vector3D;
import processing.core.PApplet;
import processing.core.PMatrix;

public class EmCityLuciService extends LcRemoteService {
	public final String scenarioName = "EmCity"; 
	private int ScID = 0, cameraID;
	boolean didReceiveCameraID = false;
	private EmCity emc;
	public EmCityLuciService(DefaultArgsProcessor ap) {
		super(ap);
	}
	
	public void setEmCity(EmCity emc){
		this.emc = emc;
	}

	@Override
	public String getDescription() {
		return "Expects an optional typology file (for now; might accept settings in the future).";
	}

	@Override
	protected ResponseHandler newResponseHandler() {
		// TODO Auto-generated method stub
		return new RemoteServiceResponseHandler() {
			
			@Override
			public void processResult(Message m) {
//				System.out.println(m);
			}
			
			@Override
			public Message implementation(Message input) {
				JSONObject h = input.getHeader();
				System.out.println(h);
				AttachmentAsArray a = (AttachmentAsArray) input.getAttachment(0);
				
				Reader r = new Reader();
				List<Cluster> updatedClusters = new LinkedList<>();
				List<Integer> deletedIDs = emc.updateTypologies(r.lines(a.getByteBuffer()), updatedClusters);
				uploadClusters(updatedClusters, deletedIDs);
				return new Message(new JSONObject().put("result", new JSONObject()
						.put("updatedClusters", updatedClusters.size())
						.put("deletedClusters", deletedIDs.size())));
			}
		};
	}

	@Override
	protected JSONObject exampleCall() {
		return new JSONObject("{'run':'EmCity'}");
	}

	@Override
	protected JSONObject specifyInputs() {
		return new JSONObject("{'run':'EmCity','OPT typology':'attachment'}");
	}

	@Override
	protected JSONObject specifyOutputs() {
		return new JSONObject("{'XOR result':{'success':'string'}, 'XOR error':'string'}");
	}
	
	public static void main( String[] args ){
		Log.set(Log.LEVEL_TRACE);
    	DefaultArgsProcessor asp = new DefaultArgsProcessor(args);
		System.out.println("EmCity registering at "+asp.getHostname()+":"+asp.getPort());
		EmCity emc = new EmCity();
		EmCityLuciService em = new EmCityLuciService(asp);
		emc.setLuci(em);
		em.setEmCity(emc);
		new Thread(em).start();
		em.connect(asp.getHostname(), asp.getPort());
		PApplet.runSketch(new String[]{""}, emc);
    }
	
	public void createScenario(Consumer<Integer> onScenarioCreated){
		sendAndReceive(
				new Message(new JSONObject().put("run", "scenario.GetList")), 
				new ResponseHandler(){
			@Override
			public void processResult(Message m) {
				JSONObject r = m.getHeader().getJSONObject("result");
				List<Integer> ScIDs = StreamSupport.stream(r.getJSONArray("scenarios").spliterator(), false)
						.filter(o -> ((JSONObject) o).getString("name").equals(scenarioName))
						.map(o -> ((JSONObject) o).getInt("ScID"))
						.collect(Collectors.toList());
				if (ScIDs.size() > 0){
					System.out.printf("deleting %d", ScID);
					sendAndReceive(
						new Message(new JSONObject()
							.put("run", "scenario.Delete")
							.put("ScIDs", ScIDs)
						),
						new ResponseHandler() {
							@Override
							public void processResult(Message m) {
								_createScenario(onScenarioCreated);
							}
							public void processError(Message m) {
								System.err.println(m.getHeader().getString("error"));
								_createScenario(onScenarioCreated);
							}
						}
					);
				} else _createScenario(onScenarioCreated);
			}
		});
	}
	
	private void _createScenario(Consumer<Integer> onScenarioCreated){
		JSONObject request = new JSONObject()
				.put("run", "scenario.Create")
				.put("name", scenarioName);
		sendAndReceive(new Message(request), new ResponseHandler(){
			@Override
			public void processResult(Message m) {
				JSONObject h = m.getHeader();
				ScID = h.getJSONObject("result").getInt("ScID");
				onScenarioCreated.accept(ScID);
			}
		});
	}

	/**Generates GeoJSON geometry
	 * @param clusters
	 * @param deletedIDs
	 */
	public void uploadClusters(List<Cluster> clusters, List<Integer> deletedIDs) {
		final JSONArray features = new JSONArray();
		final JSONObject geojson = new JSONObject()
				.put("type", "FeatureCollection")
				.put("features", features);
		clusters.stream().forEach(cl -> features.put(new JSONObject()
				.put("type", "Feature")
				.put("properties", new JSONObject().put("geomID", cl.getLuciID()))
				.put("geometry", new JSONObject()
						.put("type", "MultiPolygon")
						.put("coordinates", cl.getSurfacePolygons()))
				));
		if (deletedIDs != null){
			features.put(new JSONObject()
					.put("type", "Feature")
					.put("properties", new JSONObject().put("deletedIDs", deletedIDs)));
		}
		Message m = new Message(new JSONObject()
				.put("run", "scenario.geojson.Update")
				.put("ScID", ScID)
				.put("geometry_input", new JSONObject()
						.put("format", "geojson")
						.put("geometry", geojson))
				);
		sendAndReceive(m, new ResponseHandler() {
			@Override
			public void processResult(Message m) {
				List<Integer> newIDs = JSON.ArrayToIntList(m.getHeader()
						.getJSONObject("result").getJSONArray("newIDs"));
				for (int i = 0; i < newIDs.size(); i++){
					clusters.get(i).setLuciID(newIDs.get(i));
				}
			}
		});
	}
	
	public void publishCamera(PeasyCam cam){
		JSONObject jCam = peasyCamToJSONObject(cam);
		if (!didReceiveCameraID){
			sendAndReceive(new Message(new JSONObject().put("run", "scenario.camera.List")), 
				new ResponseHandler() {
				@Override
				public void processResult(Message m) {
					List<Integer> ids = JSON.ArrayToIntList(m.getHeader().getJSONObject("result")
							.getJSONArray("cameraIDs"));
					if (ids.size() > 0){
						cameraID = ids.get(0);
						didReceiveCameraID = true;
						send(new Message(jCam
								.put("run", "scenario.camera.Update")
								.put("cameraID", cameraID)));
					} else {
						sendAndReceive(new Message(jCam
								.put("run", "scenario.camera.Create")
								.put("scale", 1)),
								new ResponseHandler() {
									@Override
									public void processResult(Message m) {
										cameraID = m.getHeader().getJSONObject("result").getInt("cameraID");
									}
								});
					}
					
				}
			});
		} else {
			send(new Message(jCam
					.put("run", "scenario.camera.Update")
					.put("cameraID", cameraID)));
		}
	}
	
	public JSONObject peasyCamToJSONObject(PeasyCam cam){
		System.out.println(Arrays.toString(cam.getRotations()));
		System.out.println(Arrays.toString(emc.getMatrix().get(null)));
//		float[] r = cam.getRotations();
//		Rotation rotX = new Rotation(new Vector3D(1,0,0), r[0]);
//		Rotation rotY = new Rotation(new Vector3D(0,1,0), r[1]);
//		Rotation rotZ = new Rotation(new Vector3D(0,0,1), r[2]);
//		Rotation rot = rotX.applyTo(rotY).applyTo(rotZ);
//		Vector3D camUp = rot.applyTo(new Vector3D(0,-1,0));
		
		PMatrix m = emc.getMatrix();
		float[] camUp = new float[4];
		PMatrix cm = m.get();
		cm.invert();
		cm.mult(new float[]{0, 1, 0, 0}, camUp);
		
//		Vector3D camUp = cam.getRotation().applyTo(new Vector3D(0,1,0));
		
//		List<Float> rotX = Arrays.asList(new Float[]{
//					1f, 0f, 0f,
//					0f, (float) Math.cos(r[0]), (float) -Math.sin(r[0]),
//					0f, (float) Math.sin(r[0]), (float) Math.cos(r[0])
//		});
//		List<Float> rotY = Arrays.asList(new Float[]{
//					(float) Math.cos(r[1]), 0f, (float) Math.sin(r[1]),
//					0f, 1f, 0f,
//					(float) -Math.sin(r[0]), 0f, (float) Math.cos(r[0])
//		});
//		List<Float> rotZ = Arrays.asList(new Float[]{
//					(float) Math.cos(r[0]), (float) -Math.sin(r[0]), 0f,
//					(float) Math.sin(r[0]), (float) Math.cos(r[0]), 0f,
//					0f, 0f, 1f
//		});
		
		
		
		return new JSONObject()
				.put("lookAt", new JSONObject()
						.put("x",  f(cam.getLookAt()[0]))
						.put("y", f(cam.getLookAt()[1]))
						.put("z",  f(cam.getLookAt()[2])))
				.put("cameraUp", new JSONObject()
//						.put("x",  f(camUp.getX()))
//						.put("y",  f(camUp.getY()))
//						.put("z",  f(camUp.getZ()))
						.put("x",  f(camUp[0]))
						.put("y",  f(camUp[1]))
						.put("z",  f(camUp[2]))
//						.put("x", 0)
//						.put("y", 1)
//						.put("z", 0)
						)
				.put("location", new JSONObject()
						.put("x",  f(cam.getPosition()[0]))
						.put("y", f(-cam.getPosition()[1]))
						.put("z",  f(cam.getPosition()[2])));
	}
	
	private float f(float f){
		if (f == -0) return 0.0f;
		return f;
	}
	
	private float f(double f){
		if (f == -0) return 0.0f;
		return (float)f;
	}

}

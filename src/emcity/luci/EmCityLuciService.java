package emcity.luci;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esotericsoftware.minlog.Log;

import emcity.Cluster;
import emcity.EmCity;
import emcity.Reader;
import luci.connect.AttachmentAsArray;
import luci.connect.LcRemoteService;
import luci.connect.Message;
import processing.core.PApplet;

public class EmCityLuciService extends LcRemoteService {
	private int ScID = 0;
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
				// TODO Auto-generated method stub
				
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
		JSONObject request = new JSONObject().put("run", "scenario.Create").put("name", "EmCity");
		sendAndReceive(new Message(request), new ResponseHandler(){

			@Override
			public void processResult(Message m) {
				JSONObject h = m.getHeader();
				ScID = h.getJSONObject("result").getInt("ScID");
				onScenarioCreated.accept(ScID);
			}
			
		});
	}

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
					.put("type", "feature")
					.put("properties", new JSONObject().put("deletedIDs", deletedIDs)));
		}
		Message m = new Message(new JSONObject()
				.put("run", "scenario.geojson.Update")
				.put("ScID", ScID)
				.put("geometry_input", new JSONObject()
						.put("format", "geojson")
						.put("geometry", geojson))
				);
//		for (Object f: features){
//			JSONObject o = (JSONObject) f;
//			System.out.println(o.toString(4));
//		}
		send(m);
	}

}

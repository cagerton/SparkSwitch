import static spark.Spark.*;
import spark.*;

import com.heroicrobot.dropbit.registry.DeviceRegistry;
import com.heroicrobot.dropbit.devices.pixelpusher.Pixel;
import com.heroicrobot.dropbit.devices.pixelpusher.Strip;

import org.apache.log4j.BasicConfigurator;

import java.util.*;

class StripInitObserver implements Observer {
	public boolean hasStrips = false;
	public void update(Observable registry, Object updatedDevice) {
		this.hasStrips = true;
		DeviceRegistry r = (DeviceRegistry) registry;
    r.startPushing();
    r.setAutoThrottle(true);
    System.out.println("Boom. Started pusher.");
	}
}

public class HelloLamp {
	static DeviceRegistry registry;
	static StripInitObserver stripObserver;

	public static void main(String[] args) {
		BasicConfigurator.configure();
		registry = new DeviceRegistry();
		stripObserver = new StripInitObserver();
		registry.addObserver(stripObserver);

		// Eclipse does something weird here:
		//staticFileLocation("public");
		externalStaticFileLocation(System.getProperty("user.dir") + "/public"); 

		get(new Route("/") {
      public Object handle(Request request, Response response) {
        response.redirect("/index.html");
        return "";
      }
		});
		
		post(new Route("/update") {
			@Override
			public Object handle(Request request, Response response) {

				Pixel p = new Pixel();
				QueryParamsMap q = request.queryMap(); 
				p.red = (byte) (int) q.get("red").integerValue();
				p.green = (byte) (int) q.get("green").integerValue();
				p.blue = (byte) (int) q.get("blue").integerValue();
				p.orange = (byte) (int) q.get("orange").integerValue();
				p.white = (byte) (int) q.get("white").integerValue();
				System.out.format("R:%d G:%d B:%d O:%d W:%d\n", p.red &0xFF, p.green&0xFF, p.blue&0xFF, p.orange&0xFF, p.white&0xFF);

				for (Strip strip : registry.getStrips()) {
					strip.setRGBOW(true);
					for (int stripx = 0; stripx < strip.getLength(); stripx++) {
					  strip.setPixel(p, stripx);
					}
				}
				return "Update complete.";
			}
		});
	}
}

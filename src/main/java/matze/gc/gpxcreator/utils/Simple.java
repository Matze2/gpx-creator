package matze.gc.gpxcreator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Properties;

import javax.xml.bind.JAXBException;

import com.groundspeak.cache._1._0._1.Cache;
import com.groundspeak.cache._1._0._1.Cache.Attributes;
import com.groundspeak.cache._1._0._1.Cache.Attributes.Attribute;
import com.groundspeak.cache._1._0._1.Cache.LongDescription;
import com.groundspeak.cache._1._0._1.Cache.Owner;
import com.groundspeak.cache._1._0._1.Cache.ShortDescription;
import com.topografix.gpx._1._0.Gpx;
import com.topografix.gpx._1._0.Gpx.Wpt;

import matze.base.jaxb.JAXBUtils;
import net.gsak.xmlv1._6.WptExtension;

public class Simple {
	private final Wpt wpt;

	public Simple(File propFile) throws ParseException, IOException {
		Properties props = loadProps(propFile);

		CacheData cacheData = new CacheData(props);
		if (!propFile.getName().equals(cacheData.getGcCode() + ".properties")) {
			throw new IllegalArgumentException(
					"Mismatch: file " + propFile.getName() + " contains GCCode "
							+ cacheData.getGcCode());
		}

		this.wpt = new Wpt();
		wpt.setName(cacheData.getGcCode());
		wpt.setDesc(cacheData.getName());
		wpt.setUrl("https://coord.info/" + cacheData.getGcCode());
		wpt.setUrlname(cacheData.getName());
		wpt.setSym("Geocache");
		wpt.setType("Geocache|" + cacheData.getType().getLabel());

		wpt.setLat(new BigDecimal(cacheData.getLatitude()));
		wpt.setLon(new BigDecimal(cacheData.getLongitude()));
		wpt.setTime(cacheData.getPlacedAt());

		Cache cache = new Cache();
		cache.setId(String.valueOf(cacheData.getId()));
		cache.setAvailable("True");
		cache.setArchived("False");
		cache.setName(cacheData.getName());
		cache.setPlacedBy(cacheData.getOwner());

		Owner ownerObj = new Owner();
		ownerObj.setValue(cacheData.getOwner());
		cache.getOwner().add(ownerObj);

		cache.setContainer(cacheData.getSize().name());
		cache.setType(cacheData.getType().getLabel());

		Attributes attributes = new Attributes();
		Attribute attribute = new Attribute();
		attribute.setInc(Byte.valueOf((byte) 1));
		attribute.setId("13");
		attribute.setValue("Zu jeder Zeit erreichbar");
		attributes.getAttribute().add(attribute);
		cache.getAttributes().add(attributes);

		cache.setDifficulty(cacheData.getDifficulty());
		cache.setTerrain(cacheData.getTerrain());
		cache.setCountry("Germany");
		cache.setState("Bayern");

		ShortDescription shortDescription = new ShortDescription();
		shortDescription.setHtml("False");
		shortDescription.setValue(cacheData.getShortDescription());
		cache.getShortDescription().add(shortDescription);

		LongDescription longDescription = new LongDescription();
		longDescription.setHtml("True");
		longDescription.setValue("No long description available");
		cache.getLongDescription().add(longDescription);

		cache.setEncodedHints("");
		wpt.getAny().add(cache);

		WptExtension wptExtension = new WptExtension();
		wptExtension.setWatch(Boolean.FALSE);
		wptExtension.setIsPremium(Boolean.TRUE);
		wptExtension.setFavPoints(BigInteger.ZERO);
		wptExtension.setGcNote("");
		wpt.getAny().add(wptExtension);
	}

	private static Properties loadProps(File propFile) throws IOException {
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(propFile)) {
			props.load(fis);
		}
		return props;
	}

	public void marshal() throws JAXBException, IOException {
		Gpx gpx = new Gpx();
		gpx.setVersion("1.0");
		gpx.setCreator("Matze");
		gpx.getWpt().add(wpt);

		File gpxFile = new File("data", wpt.getName() + ".gpx");
		try (FileOutputStream fos = new FileOutputStream(gpxFile)) {
			JAXBUtils.marshal(gpx, fos, Cache.class, WptExtension.class);
			System.out.println("Wrote GPX file " + gpxFile.getName());
		}
	}

	public static void main(String[] args)
			throws JAXBException, ParseException, IOException {
		FilenameFilter filenameFilter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.startsWith("GC") && name.endsWith(".properties");
			}
		};

		File dir = new File("data");
		for (File propFile : dir.listFiles(filenameFilter)) {
			System.out.println("Working on " + propFile.getName());
			Simple simple = new Simple(propFile);
			simple.marshal();
		}
	}
}

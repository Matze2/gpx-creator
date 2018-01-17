package matze.gc.gpxcreator.utils;

import java.text.ParseException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;

import matze.base.utils.CalendarUtils;

public class CacheData {
	public enum Size {
		Micro,
		Small,
		Regular,
		Other
	}

	public enum Type {
		Traditional("Traditional Cache"),
		Multi("Multi-Cache"),
		Unknown("Unknown Cache");

		private final String label;

		private Type(String label) {
			this.label = label;
		}

		public String getLabel() {
			return label;
		}
	}

	private static final String PROP_GCCODE = "gccode";
	private static final String PROP_TYPE = "type";
	private static final String PROP_NAME = "name";
	private static final String PROP_OWNER = "owner";
	private static final String PROP_TERRAIN = "terrain";
	private static final String PROP_DIFFICULTY = "difficulty";
	private static final String PROP_PLACED = "placed";
	private static final String PROP_LON = "lon";
	private static final String PROP_LAT = "lat";
	private static final String PROP_SIZE = "size";
	private static final String PROP_SHORTDESC = "shortdesc";

	private static final FastDateFormat DATE_PARSER1 = FastDateFormat
			.getInstance("dd.MM.yyyy", TimeZone.getTimeZone("UTC"));
	private static final FastDateFormat DATE_PARSER2 = FastDateFormat
			.getInstance("MM/dd/yyyy", TimeZone.getTimeZone("UTC"));

	private static final CoordsConverter COORDS_CONVERTER = new CoordsConverter();
	private static final GCCodeConverter GCCODE_CONVERTER = new GCCodeConverter();

	private final String gcCode;
	private final int id;
	private final Type type;
	private final String name;
	private final String owner;
	private final double latitude;
	private final double longitude;
	private final Size size;
	private final String difficulty;
	private final String terrain;
	private final Calendar placedAt;
	private final String shortDescription;

	public CacheData(Properties properties) throws ParseException {
		this.gcCode = properties.getProperty(PROP_GCCODE);
		this.id = GCCODE_CONVERTER.convertToId(gcCode);
		this.type = parseType(properties.getProperty(PROP_TYPE));
		this.name = properties.getProperty(PROP_NAME);
		this.owner = properties.getProperty(PROP_OWNER);
		this.latitude = COORDS_CONVERTER
				.convertToDecimal(false, properties.getProperty(PROP_LAT));
		this.longitude = COORDS_CONVERTER
				.convertToDecimal(true, properties.getProperty(PROP_LON));
		this.size = Size.valueOf(properties.getProperty(PROP_SIZE));
		this.difficulty = parseDT(properties.getProperty(PROP_DIFFICULTY));
		this.terrain = parseDT(properties.getProperty(PROP_TERRAIN));
		this.placedAt = getPlacedDate(properties);
		this.shortDescription = properties
				.getProperty(PROP_SHORTDESC, "No short description available");
	}

	private static Type parseType(String value) {
		if (value == null) {
			return Type.Traditional;
		}
		return Type.valueOf(value);
	}

	private static String parseDT(String value) {
		double level = Double.parseDouble(value);
		double rest = level - (int)level;
		if (rest == 0) {
			return String.valueOf((int)level);
		}
		if (rest == 0.5) {
			return String.valueOf(level);
		}
		throw new IllegalArgumentException("D/T must be integer or .5");
	}

	private static GregorianCalendar getPlacedDate(Properties props)
			throws ParseException {
		String placedDate = props.getProperty(PROP_PLACED);

		FastDateFormat dateFormat = placedDate.indexOf('/') >= 0 ? DATE_PARSER2 : DATE_PARSER1;
		GregorianCalendar calendar = CalendarUtils
				.toCalendar(dateFormat.parse(placedDate));
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		return calendar;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public String getGcCode() {
		return gcCode;
	}

	public int getId() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public String getOwner() {
		return owner;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public Size getSize() {
		return size;
	}

	public String getDifficulty() {
		return difficulty;
	}

	public String getTerrain() {
		return terrain;
	}

	public Calendar getPlacedAt() {
		return placedAt;
	}
}

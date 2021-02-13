package mineverse.Aust1n46.chat.utilities;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.common.collect.ImmutableList;

//This class is used to query the Mojang servers to verify UUID's.
public class UUIDFetcher implements Callable<Map<String, UUID>> { //unimplemented
	private static final double PROFILES_PER_REQUEST = 100;
	private static final String PROFILE_URL = "https://api.mojang.com/profiles/minecraft";
	private final JSONParser jsonParser = new JSONParser();
	private final List<String> names;
	private final boolean rateLimiting;

	public UUIDFetcher(List<String> names, boolean rateLimiting) {
		this.names = ImmutableList.copyOf(names);
		this.rateLimiting = rateLimiting;
	}

	public UUIDFetcher(List<String> names) {
		this(names, true);
	}

	public Map<String, UUID> call() throws Exception {
		Map<String, UUID> uuidMap = new HashMap<String, UUID>();
		int requests = (int) Math.ceil(names.size() / PROFILES_PER_REQUEST);
		for(int i = 0; i < requests; i++) {
			HttpURLConnection connection = createConnection();
			String body = JSONArray.toJSONString(names.subList(i * 100, Math.min((i + 1) * 100, names.size())));
			writeBody(connection, body);
			JSONArray array = (JSONArray) jsonParser.parse(new InputStreamReader(connection.getInputStream()));
			for(Object profile : array) {
				JSONObject jsonProfile = (JSONObject) profile;
				String id = (String) jsonProfile.get("id");
				String name = (String) jsonProfile.get("name");
				UUID uuid = UUIDFetcher.getUUID(id);
				uuidMap.put(name, uuid);
			}
			if(rateLimiting && i != requests - 1) {
				Thread.sleep(100L);
			}
		}
		return uuidMap;
	}

	private static void writeBody(HttpURLConnection connection, String body) throws Exception {
		OutputStream stream = connection.getOutputStream();
		stream.write(body.getBytes());
		stream.flush();
		stream.close();
	}

	private static HttpURLConnection createConnection() throws Exception {
		URL url = new URL(PROFILE_URL);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private static UUID getUUID(String id) {
		return UUID.fromString(id.substring(0, 8) + "-" + id.substring(8, 12) + "-" + id.substring(12, 16) + "-" + id.substring(16, 20) + "-" + id.substring(20, 32));
	}

	public static byte[] toBytes(UUID uuid) {
		ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
		byteBuffer.putLong(uuid.getMostSignificantBits());
		byteBuffer.putLong(uuid.getLeastSignificantBits());
		return byteBuffer.array();
	}

	public static UUID fromBytes(byte[] array) {
		if(array.length != 16) {
			throw new IllegalArgumentException("Illegal byte array length: " + array.length);
		}
		ByteBuffer byteBuffer = ByteBuffer.wrap(array);
		long mostSignificant = byteBuffer.getLong();
		long leastSignificant = byteBuffer.getLong();
		return new UUID(mostSignificant, leastSignificant);
	}

	public static UUID getUUIDOf(String name) throws Exception {
		return new UUIDFetcher(Arrays.asList(name)).call().get(name);
	}
	
	/**
     * Returns whether the passed UUID is a v3 UUID. Offline UUIDs are v3, online are v4.
     * @param uuid the UUID to check
     * @return whether the UUID is a v3 UUID & thus is offline
     */
    public static boolean uuidIsOffline(UUID uuid) {
        return uuid.version() == 3;
    }
    
    public static UUID getUUIDFromPlayer(Player player) {
    	UUID uuid = player.getUniqueId();
    	if(uuidIsOffline(uuid)) {
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - Detected Offline UUID!"));
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - If you are using BungeeCord, make sure you have properly setup IP Forwarding."));
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - https://www.spigotmc.org/wiki/bungeecord-ip-forwarding/"));
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - You can access this wiki page from the log file or just Google it."));
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - If you're running a \"cracked\" server, player data might not be stored properly, and thus, you are on your own."));
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - If you run your server in offline mode, you might have to reset your player data when switching to online mode!"));
    		Bukkit.getConsoleSender().sendMessage(Format.FormatStringAll("&8[&eVentureChat&8]&c - No player data will be saved in offline mode unless you set the \"cracked\" server acknowledgement in the config!"));
    	}
    	return uuid;
    }
}

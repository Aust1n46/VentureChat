package venture.Aust1n46.chat.model;

import java.util.List;

import lombok.Getter;

@Getter
public class JsonFormat {
	private List<JsonAttribute> jsonAttributes;
	private int priority;
	private String name;

	public JsonFormat(String name, int priority, List<JsonAttribute> jsonAttributes) {
		this.name = name;
		this.priority = priority;
		this.jsonAttributes = jsonAttributes;
	}
}

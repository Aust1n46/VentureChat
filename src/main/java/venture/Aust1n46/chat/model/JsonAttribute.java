package venture.Aust1n46.chat.model;

import java.util.List;

import lombok.Getter;

@Getter
public class JsonAttribute {
	private String name;
	private List<String> hoverText;
	private String clickAction;
	private String clickText;

	public JsonAttribute(String name, List<String> hoverText, String clickAction, String clickText) {
		this.name = name;
		this.hoverText = hoverText;
		this.clickAction = clickAction;
		this.clickText = clickText;
	}
}

package venture.Aust1n46.chat.model;

import java.util.List;

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
	
	public String getName() {
		return name;
	}
	
	public List<String> getHoverText() {
		return hoverText;
	}
	
	public String getClickAction() {
		return clickAction;
	}
	
	public String getClickText() {
		return clickText;
	}
}

package mineverse.Aust1n46.chat.json;

import java.util.List;

//This class is used to create JsonFormat objects using data from the config file.
public class JsonFormat {
	private List<String> hoverTextName;
	private List<String> hoverTextPrefix;
	private List<String> hoverTextSuffix;
	private String clickName;
	private String clickNameText;
	private String clickPrefix;
	private String clickPrefixText;
	private String clickSuffix;
	private String clickSuffixText;
	private int priority;
	private String name;
	
	public JsonFormat(String name, int priority, List<String> hoverTextName, String clickName, String clickNameText, List<String> hoverTextPrefix, String clickPrefix, String clickPrefixText, String clickSuffix, String clickSuffixText, List<String> hoverTextSuffix) {
		this.name = name;
		this.priority = priority;
		this.hoverTextName = hoverTextName;
		this.clickNameText = clickNameText;
		this.hoverTextPrefix = hoverTextPrefix;
		this.clickPrefix = clickPrefix;
		this.clickPrefixText = clickPrefixText;
		this.clickName = clickName;
		this.clickSuffix = clickSuffix;
		this.clickSuffixText = clickSuffixText;
		this.hoverTextSuffix = hoverTextSuffix;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getClickName() {
		return this.clickName;
	}
	
	public String getClickNameText() {
		return this.clickNameText;
	}
	
	public String getClickSuffix() {
		return this.clickSuffix;
	}
	
	public String getClickSuffixText() {
		return this.clickSuffixText;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public List<String> getHoverTextName() {
		return this.hoverTextName;
	}
	
	public List<String> getHoverTextPrefix() {
		return this.hoverTextPrefix;
	}
	
	public List<String> getHoverTextSuffix() {
		return this.hoverTextSuffix;
	}
	
	public String getClickPrefix() {
		return this.clickPrefix;
	}
	
	public String getClickPrefixText() {
		return this.clickPrefixText;
	}
}
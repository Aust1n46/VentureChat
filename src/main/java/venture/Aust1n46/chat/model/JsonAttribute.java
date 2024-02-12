package venture.Aust1n46.chat.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JsonAttribute {
	private String name;
	private List<String> hoverText;
	private ClickAction clickAction;
	private String clickText;
}

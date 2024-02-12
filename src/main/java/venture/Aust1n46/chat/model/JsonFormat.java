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
public class JsonFormat {
	private String name;
	private int priority;
	private List<JsonAttribute> jsonAttributes;
}

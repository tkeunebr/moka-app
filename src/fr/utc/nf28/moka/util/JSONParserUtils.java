package fr.utc.nf28.moka.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import fr.utc.nf28.moka.data.ComputerItem;
import fr.utc.nf28.moka.data.MediaItem;
import fr.utc.nf28.moka.data.MokaItem;
import fr.utc.nf28.moka.data.TextItem;
import fr.utc.nf28.moka.io.agent.A2ATransaction;

/**
 * a JSON serializer/deserializer that uses Jackson
 */
public final class JSONParserUtils {
	private static final ObjectMapper sMapper = new ObjectMapper();

	public static String serializeA2ATransaction(final A2ATransaction transaction) throws IOException {
		return sMapper.writeValueAsString(transaction);
	}

	public static A2ATransaction deserializeA2ATransaction(final String json) throws IOException {
		final JsonNode rootNode = sMapper.readTree(json);
		final JsonNode typeNode = rootNode.get("type");
		final JsonNode contentNode = rootNode.get("content");
		final JsonNode contentClassNode = rootNode.get("contentClass");

		if (typeNode != null && contentNode != null && contentClassNode != null) {
			Class contentClass = Object.class;
			try {
				contentClass = Class.forName(contentClassNode.asText());
			} catch (ClassNotFoundException e) {
				System.out.println("deserializeA2ATransaction:ClassNotFound : " + contentClassNode.asText());
				System.out.println("use " + contentClass.toString() + " instead");
			}
			return new A2ATransaction(typeNode.asText(), sMapper.treeToValue(contentNode, contentClass));
		}

		return null;
	}

	public static MokaItem deserializeItemEntry(final JsonNode rootNode) throws IOException {
		MokaItem result = null;

		//retrieve common stuff
		final String type = rootNode.path("type").asText();
		final String title = rootNode.path("title").asText();
		final int id = rootNode.path("id").asInt();
		final String creationDate = rootNode.path("creationDate").asText();

		if ("umlClass".equals(type)) {
			result = new ComputerItem.UmlItem(title);
			//retrieve uml specific stuff
		} else if ("image".equals(type)) {
			result = new MediaItem.ImageItem(title);
			//retrieve image specific stuff
		} else if ("post-it".equals(type)) {
			result = new TextItem.PostItItem(title);
			//retrieve post-it specific stuff
		} else if ("video".equals(type)) {
			result = new MediaItem.VideoItem(title);
		}

		if (result != null) {
			result.setId(id);
			result.setCreationDate(creationDate);
		}
		return result;
	}

	public static List<MokaItem> deserializeItemEntries(final String json) throws IOException {
		final JsonNode rootNode = sMapper.readTree(json);
		List<MokaItem> result = Collections.emptyList();
		if (rootNode.isArray()) {
			result = new ArrayList<MokaItem>(rootNode.size());
			for (Iterator<JsonNode> iter = rootNode.elements(); iter.hasNext(); ) {
				final MokaItem item = deserializeItemEntry(iter.next());
				if (item != null) {
					result.add(item);
				}
			}
		}
		return result;
	}

}

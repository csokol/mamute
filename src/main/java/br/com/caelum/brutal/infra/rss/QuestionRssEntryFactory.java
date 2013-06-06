package br.com.caelum.brutal.infra.rss;

import static java.util.Arrays.asList;

import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import br.com.caelum.brutal.model.Question;
import br.com.caelum.brutal.model.User;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

public class QuestionRssEntryFactory {

	public void writeEntry(Question question, OutputStream output) {
		
		User author = question.getAuthor();
		RssImageEntry imageEntry = new RssImageEntryBuilder()
			.withUrl(author.getSmallPhoto())
			.withLink("http://www.guj.com.br/usuario/" + author.getId() + "/" + author.getSluggedName())
			.withTitle(author.getRealName()).build();
		
		RssEntry entry = new RssEntryBuilder()
				.withAuthor(author.getName())
				.withTitle(question.getTitle())
				.withLink(
						"http://www.guj.com.br/" + question.getId() + "-"
								+ question.getSluggedTitle())
				.withId(question.getId().toString())
				.withDate(question.getCreatedAt())
				.withImage(imageEntry).build();
		
		XStream xstream = buildXstream();

		xstream.processAnnotations(RssEntry.class);
		xstream.toXML(entry, output);
	}

	private XStream buildXstream() {
		return new XStream(new XppDriver() {
			public HierarchicalStreamWriter createWriter(Writer out) {
				return new PrettyPrintWriter(out) {
					List<String> cdataFields = asList("title", "author");
					boolean cdata = false;
					
					public void startNode(String name, Class clazz) {
						super.startNode(name, clazz);
						cdata = cdataFields.contains(name);
					}
					protected void writeText(QuickWriter writer, String text) {
						if (cdata) {
							writer.write("<![CDATA[");
							writer.write(text);
							writer.write("]]>");
						} else {
							writer.write(text);
						}
					}
				};
			}
		});
	}
}

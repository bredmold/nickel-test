@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(type = Instant.class, value = InstantXmlAdapter.class)
})
package nickel.test.inject;

import com.migesok.jaxb.adapter.javatime.InstantXmlAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.time.Instant;

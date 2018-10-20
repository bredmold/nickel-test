package nickel.test.jaxb;

import nickel.test.BeanClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;

import static nickel.test.jaxb.JaxbTestResource.jaxbTestResource;
import static org.assertj.core.api.Assertions.assertThat;

public class JaxbTestResourceTest {
    @Test
    public void bindToXml() throws ClassNotFoundException, JAXBException, IOException {
        BeanClass instance = jaxbTestResource()
            .forTestMethod()
            .asXml(BeanClass.class);

        assertThat(instance).isEqualTo(new BeanClass(25));
    }

    @Test
    public void customContext() throws JAXBException, ClassNotFoundException, IOException {
        BeanClass instance = jaxbTestResource()
            .forTestMethod()
            .withJaxbContext(JAXBContext.newInstance(BeanClass.class))
            .asXml(BeanClass.class);

        assertThat(instance).isEqualTo(new BeanClass(30));
    }
}

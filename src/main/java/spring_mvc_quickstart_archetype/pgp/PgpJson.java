package spring_mvc_quickstart_archetype.pgp;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PgpJson {
    public String pgppriv;
    public String pgppub;

    public PgpJson() {}

    public PgpJson(String pgppriv, String pgppub) {
      this.pgppriv = pgppriv;
      this.pgppub = pgppub;
    }
}

package spring_mvc_quickstart_archetype.pad;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.security.Principal;

import com.bitbucket.thinbus.srp6.spring.SrpAccountEntity;
import com.bitbucket.thinbus.srp6.spring.SrpAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.xml.bind.annotation.XmlRootElement;
            
@Controller
public class PadController {

    private static final Logger LOGGER = Logger.getLogger("pad");

	@RequestMapping(value = "/pad", method = RequestMethod.GET)
	public String input() {
		return "pad/pad";
	}

    @Autowired
    private SrpAccountRepository accountRepository;

    @RequestMapping(
        value = "/setpad",
        method = RequestMethod.POST,
        produces = "application/json"
    )
    @ResponseBody
    public String savePad(@RequestParam(value="text", required=true) final String text, Principal principal) {
        if (principal != null) {
            SrpAccountEntity account = accountRepository.findByEmail(principal.getName());
            account.setText(text);
            account = accountRepository.update(account);
            return "{\"status\": true}";
        }
        return "{\"status\": false}";
    }

    @RequestMapping(
        value = "/getpad",
        method = RequestMethod.GET,
        produces = "text/plain"
    )
    @ResponseBody
    public String loadPad(Principal principal) {
        if (principal != null) {
            SrpAccountEntity account = accountRepository.findByEmail(principal.getName());
            LOGGER.warning("plop");
            return account.getText();
        }
        return "";
    }
}

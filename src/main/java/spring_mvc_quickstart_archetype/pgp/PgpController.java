package spring_mvc_quickstart_archetype.pgp;

import java.security.Principal;

import com.bitbucket.thinbus.srp6.spring.SrpAccountEntity;
import com.bitbucket.thinbus.srp6.spring.SrpAccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PgpController {
    @Autowired
    private SrpAccountRepository accountRepository;

    @RequestMapping(
        value = "/pgp",
        method = RequestMethod.GET,
        produces = "application/json"
    )
    @ResponseBody
    public PgpJson getPgp(Principal principal) {
        if (principal != null) {
            SrpAccountEntity account = accountRepository.findByEmail(principal.getName());
            return new PgpJson(account.getPgppriv(), account.getPgppub());
        } else {
            return new PgpJson("", "");
        }
    }
}

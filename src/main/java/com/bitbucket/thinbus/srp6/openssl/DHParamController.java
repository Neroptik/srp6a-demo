package com.bitbucket.thinbus.srp6.openssl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.bitbucket.thinbus.srp6.js.OpenSSLCryptoConfigConverter;;

@Controller
public class DHParamController {
	@RequestMapping(value = "/dhparam", method = RequestMethod.GET)
	public String input() {
		return "dhparam/input";
	}
	
	@RequestMapping(value = "/dhparam", method = RequestMethod.POST)
	public String output(@RequestParam(value="hash", required=true) final String hash, @RequestParam(value="dhparam", required=true) final String dhparam, Model model) throws ServletException {
		final List<String> rawLines = Arrays.asList(dhparam.split("\n"));
		final List<String> lines = new ArrayList<String>(rawLines.size());
		for( String raw : rawLines ) {
			lines.add(raw.trim());
		}
		final OpenSSLCryptoConfigConverter openSSLCryptoConfig = new OpenSSLCryptoConfigConverter();
		try {
			StringBuilder builder = new StringBuilder();
			for( String line : openSSLCryptoConfig.run(hash, lines))  {
				builder.append(line+"\n");
			}
			model.addAttribute("lines", builder.toString());
		} catch (Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
		return "dhparam/output";
	}	
}

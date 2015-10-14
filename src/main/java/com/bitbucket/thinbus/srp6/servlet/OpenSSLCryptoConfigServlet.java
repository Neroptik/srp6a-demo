package com.bitbucket.thinbus.srp6.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bitbucket.thinbus.srp6.js.OpenSSLCryptoConfig;

public class OpenSSLCryptoConfigServlet extends HttpServlet {
	private static final long serialVersionUID = 4587551976448645302L;

	public OpenSSLCryptoConfigServlet(){}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Thinbus Configuration Generator</h1>");
        response.getWriter().println("<p>This page will parse the output of running 'openssl dhparam -text' and output the constants needed to configure <a href='https://bitbucket.org/simon_massey/thinbus-srp-js/overview'>Thinbus SRP</a></p>");
        response.getWriter().println("Create your large safe prime using openssl with the following command whick backsup the output into /tmp/my_dhparam.txt:");
        response.getWriter().println("<pre># create your parameters set <bit-length> (recommended minimum of 2048)");
        response.getWriter().println("openssl dhparam -text <bit-length> | tee /tmp/my_dhparam.txt</pre>");
        response.getWriter().println("<p>Paste the output of that command into the textarea below and select the hash algorithm below (this page only supports Java8 hash algorithms):</p>");
        response.getWriter().println("<form action=\"/OpenSSLCryptoConfigServlet\" id=\"usrform\" method=\"post\">");
        response.getWriter().println("  Hash: <input type=\"text\" name=\"hash\" value=\"SHA-256\"><br/>");
        response.getWriter().println("  <textarea rows=\"25\" cols=\"90\" name=\"dhparam\" form=\"usrform\">");
        response.getWriter().println("  </textarea><br/>");
        response.getWriter().println("  <input type=\"submit\">");
        response.getWriter().println("</form>");
    }

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setStatus(HttpServletResponse.SC_OK);
		final String dhparam = (String)req.getParameter("dhparam");
		final String hash = (String)req.getParameter("hash");
		final List<String> rawLines = Arrays.asList(dhparam.split("\n"));
		final List<String> lines = new ArrayList<String>(rawLines.size());
		for( String raw : rawLines ) {
			lines.add(raw.trim());
		}
		final OpenSSLCryptoConfig openSSLCryptoConfig = new OpenSSLCryptoConfig();
		try {
			for( String line : openSSLCryptoConfig.run(hash, lines))  {
				response.getWriter().println(line+"<br/>");
			}
		} catch (Exception e) {
			throw new ServletException(e.getMessage(), e);
		}
	}	
	
}

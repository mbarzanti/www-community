/**
* OWASP Benchmark Project v1.2
*
* This file is part of the Open Web Application Security Project (OWASP)
* Benchmark Project. For details, please see
* <a href="https://www.owasp.org/index.php/Benchmark">https://www.owasp.org/index.php/Benchmark</a>.
*
* The OWASP Benchmark is free software: you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free Software Foundation, version 2.
*
* The OWASP Benchmark is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* @author Nick Sanidas <a href="https://www.aspectsecurity.com">Aspect Security</a>
* @created 2015
*/

/** From: Iago
   Java/tainting:
   This is a test that comes from the OWASP Benchmark v1.2.
   Here DeepSemgrep doesn't report lines 56, 128, and 195.
   But these are actually false positives! This benchmark tries
   to confuse analyzers into reporting these false positives.
   It does this in two ways, 1) by using a third-function
   `doSomething` that receives tainted data, even though it
   returns safe data; and 2) by putting both safe and unsafe
   data into a `HashMap`, but ultimately only returning the
   safe data. FOSS Semgrep falls into the first trap.
   DeepSemgrep does inter-procedural analysis so it is only
   affected by the second trap, but it seems to not fall
   into it because we are lacking a `pattern-propagators` spec
   for `HashMap`s. If we told DeepSemgrep that `HashMap`s
   store/propagate taint, then it should report the same
   false positives.
*/

package org.owasp.benchmark.testcode;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value="/xss-04/BenchmarkTest02229")
class BenchmarkTest02229NDRW extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        java.util.Map<String,String[]> map = request.getParameterMap();
        String param = "";
        if (!map.isEmpty()) {
            String[] values = map.get("BenchmarkTest02229");
            if (values != null) param = values[0];
        }


        String bar = doSomething(request, param);

		response.setHeader("X-XSS-Protection", "0");
        Object[] obj = { "a", bar};
        // NOTE: see comment at start of file
        // ruleid: deepok: no-direct-response-writer
        response.getWriter().printf(java.util.Locale.US,"Formatted like: %1$s and %2$s.",obj);
    }  // end doPost


    private static String doSomething(HttpServletRequest request, String param) throws ServletException, IOException {

        String bar = "safe!";
        java.util.HashMap<String,Object> map26903 = new java.util.HashMap<String,Object>();
        map26903.put("keyA-26903", "a_Value"); // put some stuff in the collection
        map26903.put("keyB-26903", param); // put it in a collection
        map26903.put("keyC", "another_Value"); // put some stuff in the collection
        bar = (String)map26903.get("keyB-26903"); // get it back out
        bar = (String)map26903.get("keyA-26903"); // get safe value back out

        return bar;
    }
}

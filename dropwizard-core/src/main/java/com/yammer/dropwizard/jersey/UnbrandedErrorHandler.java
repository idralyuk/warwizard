package com.yammer.dropwizard.jersey;

import com.google.common.collect.ImmutableList;
import com.yammer.dropwizard.validation.InvalidEntityException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * A de-Jetty'd version of org.eclipse.jetty.server.handler.ErrorHandler that doesn't identify the server type.
 */
public class UnbrandedErrorHandler {

    public void writeErrorPage(HttpServletRequest request, Writer writer, int code, String message, boolean showStacks)
            throws IOException
    {
        writer.write("<html>\n<head>\n");
        writeErrorPageHead(writer, code, message);
        writer.write("</head>\n<body>");
        writeErrorPageMessage(writer, code, message, request.getRequestURI());
        for (int i= 0; i < 20; i++) {
            writer.write("<br/>                                                \n");
        }
        writer.write("\n</body>\n</html>\n");
    }

    public void writeValidationErrorPage(HttpServletRequest request, StringWriter writer, InvalidEntityException exception) throws IOException {
        writer.write("<html>\n<head>\n");
        writeErrorPageHead(writer, 422, "Unprocessable Entity");
        writer.write("</head>\n<body>");
        writeInvalidationErrorPageBody(request,
                writer,
                exception.getMessage(),
                exception.getErrors());
        writer.write("\n</body>\n</html>\n");
    }

    private static void writeInvalidationErrorPageBody(HttpServletRequest request, StringWriter writer, String message, ImmutableList<String> errors) throws IOException {
        writeErrorPageMessage(writer, 422, "Unprocessable Entity", request.getRequestURI());

        writer.write("<h2>");
        write(writer, message);
        writer.write("</h2>");

        writer.write("<ul>");
        for (String error : errors) {
            writer.write("<li>");
            write(writer, error);
            writer.write("</li>");
        }
        writer.write("</ul>");
    }

    private static void writeErrorPageHead(Writer writer, int code, String message)
            throws IOException
    {
        writer.write("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\"/>\n");
        writer.write("<title>Error ");
        writer.write(Integer.toString(code));
        writer.write(' ');
        write(writer, message);
        writer.write("</title>\n");
    }

    private static void writeErrorPageMessage(Writer writer, int code, String message,String uri)
            throws IOException
    {
        writer.write("<h2>HTTP ERROR ");
        writer.write(Integer.toString(code));
        writer.write("</h2>\n<p>Problem accessing ");
        write(writer,uri);
        writer.write(". Reason:\n<pre>    ");
        write(writer,message);
        writer.write("</pre></p>");
    }

    private static void write(Writer writer, String string)
            throws IOException
    {
        if (string==null)
            return;

        for (int i=0;i<string.length();i++)
        {
            char c=string.charAt(i);

            switch(c)
            {
                case '&' :
                    writer.write("&amp;");
                    break;
                case '<' :
                    writer.write("&lt;");
                    break;
                case '>' :
                    writer.write("&gt;");
                    break;

                default:
                    if (Character.isISOControl(c) && !Character.isWhitespace(c))
                        writer.write('?');
                    else
                        writer.write(c);
            }
        }
    }

}

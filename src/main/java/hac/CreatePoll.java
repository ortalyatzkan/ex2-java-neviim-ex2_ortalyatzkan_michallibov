package hac;

import java.io.*;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.servlet.http.*;
import javax.servlet.annotation.*;



@WebServlet(urlPatterns = {"/CreatePoll"},
        initParams = {@WebInitParam(name = "CreatePoll",value = "CreatePoll")},
        loadOnStartup = 1)
public class CreatePoll extends HttpServlet {

    private Table pollResult;
    private int firstTime=0;
    private Scanner sc;

    public void init()
    {
        sc = new Scanner((getServletContext().getResourceAsStream("/" + getServletContext().getInitParameter("POLLFILE"))));
        pollResult = new Table();//save the poll and results.
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (firstTime == 0) {//if this is the first time to load the poll
            firstTime = 1;//not first time anymore
            ReadFromFile(response);//load poll
        }
        ArrayList<String> pollArr;

        response.setContentType("text/json");
        response.setHeader("Access-Control-Allow-Origin", "*");

        try (JsonWriter jsonw = Json.createWriter(response.getOutputStream())) {
            try {

                response.setStatus(HttpServletResponse.SC_OK);
                pollArr = pollResult.getResultOfPoll();

                jsonw.writeObject(buildJsonResponse(pollArr.get(0), pollArr.subList(1, pollArr.size()),pollResult.getResultOfUser()));

            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/json");
        BufferedReader reader = request.getReader();

        pollResult.setResult(reader.readLine().replace("=", ""));

        try (JsonWriter jsonw = Json.createWriter(response.getOutputStream())) {
            try {
                response.setStatus(HttpServletResponse.SC_OK);
                //Create JSON of the votes for each option
                jsonw.writeObject(buildJsonResultPoll(pollResult.getResultOfPoll(),pollResult.getResultOfUser()));


            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

            }
        }catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

        }
    }

    public void ReadFromFile(HttpServletResponse response) {
        try {
            ArrayList<String> savePollResult = new ArrayList<>();
            String numOfResults;

            while (sc.hasNextLine()) {
                numOfResults = sc.nextLine();
                savePollResult.add(numOfResults);
            }
            pollResult.setResultOfPoll(savePollResult);

            pollResult.setResultOfUser();
            //if file is too short
            if (savePollResult.size() < 3) {
                throw new IllegalArgumentException ("error");
            }

        }
        catch (Exception e) {//problem occurred during reading the poll file
            createCookiesError(response);
            throw new IllegalArgumentException("error");
        }
    }

    private JsonObject buildJsonResponse(Object pollQuestion, Object pollOptions,List<Integer> result) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        builder.add("question", pollQuestion.toString());
        builder.add("answers", pollOptions.toString().replace("[", "").replace("]",""));
        builder.add("votes",result.toString().replace("[", "").replace("]",""));
        return builder.build();
    }

    private void createCookiesError(HttpServletResponse response){
        Cookie ck = new Cookie("error", "fileError");//deleting value of cookie
        ck.setMaxAge(1000*36000);//changing the maximum age to 0 seconds
        response.addCookie(ck);//adding cookie in the response
    }



    private JsonObject buildJsonResultPoll(ArrayList<String> nameOfAnswer, List<Integer> result) {
        JsonObjectBuilder builder = Json.createObjectBuilder();
        for (int i=1 ; i<nameOfAnswer.size() ; i++)
            builder.add(nameOfAnswer.get(i),result.get(i-1).toString());
        return builder.build();
    }


        public void destroy() {}
}
package Controller;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import Model.Account;
import Model.Message;
import Service.AccountService;
import Service.MessageService;
import DAO.MessageDao;

import io.javalin.Javalin;
import io.javalin.http.Context;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    AccountService accountService;
    MessageService messageService;
    public SocialMediaController(){
        this.accountService = new AccountService();
        this.messageService = new MessageService();
    }
    public Javalin startAPI() {
        Javalin app = Javalin.create();
        app.post("/register", this::postAccountHandler);
        app.post("/login", this::postVerifyLoginHandler);
        app.post("/messages", this::postMessageHandler);
        app.get("/messages", this::getAllMessagesHandler);
        app.get("/messages/{message_id}", this::getMessageByIdHandler);
        app.delete("/messages/{message_id}", this::deleteMessageByIdHandler);
        app.patch("/messages/{message_id}", this::updateMessageByIdHandler);
        app.get("/accounts/{account_id}/messages", this::getMessagesByAccountIdHandler);

        return app;
    }
    private void postAccountHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account addedAccount = accountService.createAccount(account);
        if(addedAccount != null){
            ctx.json(mapper.writeValueAsString(addedAccount));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }
    private void postVerifyLoginHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Account account = mapper.readValue(ctx.body(), Account.class);
        Account verifiedAccount = accountService.verifyLogin(account);
        if(verifiedAccount != null){
            ctx.json(mapper.writeValueAsString(verifiedAccount));
            ctx.status(200);
        } else {
            ctx.status(401);
        }
    }
    private void postMessageHandler(Context ctx) throws JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        Message message = mapper.readValue(ctx.body(), Message.class);
        Message addedMessage = messageService.createMessage(message);
        if(addedMessage != null){
            ctx.json(mapper.writeValueAsString(addedMessage));
            ctx.status(200);
        } else {
            ctx.status(400);
        }
    }

    private void getAllMessagesHandler(Context ctx){
        List<Message> messageList = messageService.getAllMessages();
        ctx.json(messageList);
        ctx.status(200);
    }

    private void getMessageByIdHandler(Context ctx){
        int messageId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("{message_id}")));
        Message message = messageService.getMessageById(messageId);
        if(message == null){
            ctx.html("");
            ctx.status(200);
        } else {
            ctx.json(message);
            ctx.status(200);
        }
    }

    private void deleteMessageByIdHandler(Context ctx){
        int messageId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("{message_id}")));
        Message message = messageService.deleteMessageById(messageId);
        if(message == null){
            ctx.html("");
            ctx.status(200);
        } else {
            ctx.json(message);
            ctx.status(200);
        }
    }

    private void updateMessageByIdHandler(Context ctx) throws JsonProcessingException{
        int messageId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("{message_id}")));
        String newText = ctx.body().substring(18, ctx.body().length() - 3);
        Message message = messageService.updateMessage(messageId, newText);
        if(message == null){
            ctx.status(400);
        } else {
            ctx.json(message);
            ctx.status(200);
        }
    }

    private void getMessagesByAccountIdHandler(Context ctx){

            int accountId = Integer.parseInt(Objects.requireNonNull(ctx.pathParam("{account_id}")));
            List<Message> messages = messageService.getMessageByUser(accountId);
            ctx.json(messages);
            ctx.status(200);
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void exampleHandler(Context context) {
        context.json("sample text");
    }


}
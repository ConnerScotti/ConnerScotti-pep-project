package DAO;
import Model.Message;
import Util.ConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Model.Account;

public class MessageDao {
    public Message createMessage(Model.Message message){
        

        
        if(message.getMessage_text().length() > 250 || message.getMessage_text().length() == 0 || getAccountById(message.getPosted_by()) == null){
                    return null;
                }
            Connection connection = ConnectionUtil.getConnection();
        try {
                String sql = "INSERT INTO Message (posted_by, message_text, time_posted_epoch) VALUES (?, ?, ?)";        
                PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                
                stmt.setInt(1, message.getPosted_by());
                stmt.setString(2, message.getMessage_text());
                stmt.setLong(3, message.getTime_posted_epoch());
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if(rs.next()){
                    int generated_message_id = (int) rs.getLong(1);
                    return new Message(generated_message_id, message.getPosted_by(), message.getMessage_text(), message.getTime_posted_epoch());
                }
            } catch (SQLException e){
                System.out.println(e.getMessage());
            }
            return null;
    }
    public Account getAccountById(int id){

        Connection connection = ConnectionUtil.getConnection();

        try{
            String sql = "SELECT * FROM account WHERE account_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Account account = new Account(rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("password"));
                return account;
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /*## 4: Our API should be able to retrieve all messages.

As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages.

- The response body should contain a JSON representation of a list containing all messages retrieved from the database. It is expected for the list to simply be empty if there are no messages. The response status should always be 200, which is the default.
 */
    public List<Message> getAllMessages(){
        List<Message> messageList = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        try{
            String sql = "SELECT * FROM Message";
            PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));

                messageList.add(message);
    
            }
                
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return messageList;

    }


/*## 5: Our API should be able to retrieve a message by its ID.

As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/messages/{message_id}.

- The response body should contain a JSON representation of the message identified by the message_id. It is expected for the response body to simply be empty if there is no such message. The response status should always be 200, which is the default.
 */
    public Message getMessageById(int messageId){
       
        Connection connection = ConnectionUtil.getConnection();

        try{
            String sql = "SELECT * FROM message WHERE message_id = ?";

            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()){
                Message message = new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                
                        
                return message;
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

/*## 6: Our API should be able to delete a message identified by a message ID.

As a User, I should be able to submit a DELETE request on the endpoint DELETE localhost:8080/messages/{message_id}.

- The deletion of an existing message should remove an existing message from the database. If the message existed, the response body should contain the now-deleted message. The response status should be 200, which is the default.
- If the message did not exist, the response status should be 200, but the response body should be empty. This is because the DELETE verb is intended to be idempotent, ie, multiple calls to the DELETE endpoint should respond with the same type of response.
 */
    public Message deleteMessageById(int messageId){
        
        Message message = getMessageById(messageId);
        if(message == null){
            return null;
        }
        
        Connection connection = ConnectionUtil.getConnection();
        
        try {
            String sql = "DELETE FROM message WHERE message_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, messageId);
            preparedStatement.executeUpdate();
            return message;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

/*## 7: Our API should be able to update a message text identified by a message ID.

As a user, I should be able to submit a PATCH request on the endpoint PATCH localhost:8080/messages/{message_id}. The request body should contain a new message_text values to replace the message identified by message_id. The request body can not be guaranteed to contain any other information.

- The update of a message should be successful if and only if the message id already exists and the new message_text is not blank and is not over 255 characters. If the update is successful, the response body should contain the full updated message (including message_id, posted_by, message_text, and time_posted_epoch), and the response status should be 200, which is the default. The message existing on the database should have the updated message_text.
- If the update of the message is not successful for any reason, the response status should be 400. (Client error)
 */
    public Message updateMessage(int messageId, String newMessageText){
        
        Message messageCheck = getMessageById(messageId);
        if(messageCheck == null || newMessageText.isEmpty() || newMessageText.length() > 255){
            return null;
        }

        Connection connection = ConnectionUtil.getConnection();
        
        try {
            String sql = "UPDATE Message SET message_text= ? WHERE message_id = ?";
            
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, newMessageText);
            stmt.setInt(2, messageId);
            stmt.executeUpdate();
            return getMessageById(messageId);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }
 /*## 8: Our API should be able to retrieve all messages written by a particular user.

As a user, I should be able to submit a GET request on the endpoint GET localhost:8080/accounts/{account_id}/messages.

- The response body should contain a JSON representation of a list containing all messages posted by a particular user, which is retrieved from the database. It is expected for the list to simply be empty if there are no messages. The response status should always be 200, which is the default.
 */
    public List<Message> getMessageByUser(int posted_by){
        List<Message> messageList = new ArrayList<>();
        Connection connection = ConnectionUtil.getConnection();
        try {
            String sql = "SELECT * FROM message WHERE posted_by = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, posted_by);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Message message = new Message(rs.getInt("message_id"),
                        rs.getInt("posted_by"),
                        rs.getString("message_text"),
                        rs.getLong("time_posted_epoch"));
                messageList.add(message);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return messageList;
    }
}
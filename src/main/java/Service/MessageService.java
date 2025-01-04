package Service;
import DAO.MessageDao;
import Model.Message;
import java.util.List;


public class MessageService {
    private DAO.MessageDao messageDao;
    
    public MessageService(){
        this.messageDao = new MessageDao();
    }

    public MessageService(MessageDao messageDao){
        this.messageDao = messageDao;
    }

    public Message createMessage(Message message){
        return messageDao.createMessage(message);
    }

    public List<Message> getAllMessages(){
        return messageDao.getAllMessages();
    }

    public Message getMessageById(int id){
        return messageDao.getMessageById(id);
    }

    public Message deleteMessageById(int id){
        return messageDao.deleteMessageById(id);
    }

    public Message updateMessage(int id, String newText){
        return messageDao.updateMessage(id, newText);
    }

    public List<Message> getMessageByUser(int posted_by){
        return messageDao.getMessageByUser(posted_by);
    }

}
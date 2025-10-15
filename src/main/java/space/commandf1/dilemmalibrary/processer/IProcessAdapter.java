package space.commandf1.dilemmalibrary.processer;

public interface IProcessAdapter {
    void process();
    
    default void onSuccess() {}
    
    default void onFailure(Exception e) {}
    
    default void onComplete() {}
}

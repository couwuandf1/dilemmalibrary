package space.commandf1.dilemmalibrary.processer;

import java.util.ArrayList;
import java.util.List;

public class ProcesserManager {
    private final List<IProcessAdapter> processors = new ArrayList<>();
    
    public void addProcessor(IProcessAdapter processor) {
        processors.add(processor);
    }
    
    public void removeProcessor(IProcessAdapter processor) {
        processors.remove(processor);
    }
    
    public void processAll() {
        for (IProcessAdapter processor : processors) {
            try {
                processor.process();
                processor.onSuccess();
            } catch (Exception e) {
                processor.onFailure(e);
            } finally {
                processor.onComplete();
            }
        }
    }
}

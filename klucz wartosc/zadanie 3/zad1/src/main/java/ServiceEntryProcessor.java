import com.hazelcast.map.EntryProcessor;

import java.io.Serializable;
import java.util.Map;

public class ServiceEntryProcessor implements EntryProcessor<Long, Service, String>, Serializable {

    Service serviceU;

    public ServiceEntryProcessor(Service service) {
        this.serviceU = service;
    }

    @Override
    public String process(Map.Entry<Long, Service> entry) {
        Service service = entry.getValue();
        service.setClientName(serviceU.getClientName());
        service.setServiceCost(serviceU.getServiceCost());
        service.setServiceDescription(serviceU.getServiceDescription());
        System.out.println("Processing = " + service);
        entry.setValue(service);
        return "ok";
    }
}

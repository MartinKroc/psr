import java.io.Serializable;

public class Service implements Serializable {
    private static final long serialVersionUID = 1L;

    private String clientName;
    private int serviceCost;
    private String serviceDescription;

    public Service(String clientName, int serviceCost, String serviceDescription) {
        this.clientName = clientName;
        this.serviceCost = serviceCost;
        this.serviceDescription = serviceDescription;
    }

    public String getClientName() { return clientName;}
    public int getServiceCost() { return serviceCost;}
    public String getServiceDescription() { return serviceDescription;}

    public void setClientName(String clientName) { this.clientName = clientName;}
    public void setServiceCost(int serviceCost) { this.serviceCost = serviceCost;}
    public void setServiceDescription(String serviceDescription) { this.serviceDescription = serviceDescription;}

    @Override
    public String toString() { return "Usługa: " + serviceDescription + ", koszt: " + serviceCost + ", klient: " + clientName;}
}

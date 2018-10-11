import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int[] costs = new int[RouterSimulator.NUM_NODES];
  private List<Integer> neighbors = new ArrayList<>();
  private int[][] nTables;

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
    getNeighbors();
    nTables = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    broadcast();
  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
    boolean flag = false;
    
    System.out.println(Arrays.toString(pkt.mincost));
    nTables[pkt.sourceid] = pkt.mincost;
    for(int i=0; i<pkt.mincost.length;i++){
      int newPath = costs[pkt.sourceid] + pkt.mincost[i];
      if(newPath < costs[i]){
    	  flag = true;
    	  updateLinkCost(i, newPath);
      }
    }
    if(flag) broadcast();
  }


  //--------------------------------------------------
  private void sendUpdate(RouterPacket pkt) {
    sim.toLayer2(pkt);
  }


  //--------------------------------------------------
  public void printDistanceTable() {
	  myGUI.println("Current table for " + myID +
			"  at time " + sim.getClocktime());
	  myGUI.println("Distancetables:");
	  myGUI.print(F.SPACES + "dest | ");
	  for(int i=0;i<RouterSimulator.NUM_NODES;i++){
		  myGUI.print(F.SPACES + i);
	  }
	  myGUI.println("");
	  myGUI.println("-------------------------------------");
	  for (int x : neighbors) {
		  myGUI.print("  nbr"+x+"   | ");
		  for(int j=0;j<RouterSimulator.NUM_NODES;j++){
			  myGUI.print(F.SPACES + nTables[x][j]);
		  }
		  myGUI.println();
	  }
	  myGUI.println();
	  
  }

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
	  costs[dest] = newcost;
  }

  private void getNeighbors(){
    for(int i=0; i<costs.length; i++){
        if(costs[i] != RouterSimulator.INFINITY && i != myID){
          neighbors.add(i);
        }
    }

  }

  private void broadcast() {
	  for (int x : neighbors){
      RouterPacket rp = new RouterPacket(myID, x, costs);
      sendUpdate(rp);
    }
  }

}

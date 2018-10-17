import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RouterNode {
  private int myID;
  private GuiTextArea myGUI;
  private RouterSimulator sim;
  private int[] costs = new int[RouterSimulator.NUM_NODES];
  private List<Integer> neighbors = new ArrayList<>();
  private int[][] nTables;
  private int[] route = new int[RouterSimulator.NUM_NODES];
  private boolean PoisonReverse = true;

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
    getNeighbors();
    nTables = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    for(int i=0;i<nTables.length;i++) Arrays.fill(nTables[i], RouterSimulator.INFINITY);
    Arrays.fill(route, -1);
    nTables[myID] = costs;
    //System.out.println(Arrays.toString(nTables[myID]));
    broadcast();
  }

  //--------------------------------------------------
  public void recvUpdate(RouterPacket pkt) {
    boolean flag = false;
    nTables[pkt.sourceid] = pkt.mincost;
    
    for(int i = 0; i<pkt.mincost.length; i++){
    	if (i != myID) {
    		List<Integer> min = new ArrayList<>(RouterSimulator.NUM_NODES);
    		min.addAll(Collections.nCopies(RouterSimulator.NUM_NODES, RouterSimulator.INFINITY));
        	for(int x : neighbors) {
        		min.add(x, costs[x]+nTables[x][i]);
        	}
        	int min_value = Collections.min(min);
        	if(min_value != nTables[myID][i]){
        		flag = true;
        	}
        	nTables[myID][i] = min_value;
        	route[i] = min.indexOf(min_value);
        	System.out.println("To get to "+i+", I use "+route[i]);
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
	  myGUI.println(".......................");
	  myGUI.println(Arrays.toString(nTables[myID]));
	  
  }

  //--------------------------------------------------
  public void updateLinkCost(int dest, int newcost) {
	  
	  costs[dest] = newcost;
      nTables[myID][dest] = newcost;
	  broadcast();
  
  }
  
  private void poisonBroadcast(int dest){
	  System.out.println("myID : " + myID + "  dest : " + dest);
	  int[] fakeCost = nTables[myID].clone();
	  fakeCost[dest] = RouterSimulator.INFINITY;
	  
	  for (int x : neighbors){
		  if (route[x] == dest){
		      RouterPacket rp = new RouterPacket(myID, x, fakeCost);
		      sendUpdate(rp);
		  }
	    }
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
		  if(PoisonReverse){
			  int[] fakeCost = nTables[myID].clone();
			  for (int y=0;y<RouterSimulator.NUM_NODES;y++) {
				  if(route[y] == x && y!=x)
					  fakeCost[y] = RouterSimulator.INFINITY;
			  }
			  RouterPacket rp = new RouterPacket(myID, x, fakeCost);
			  sendUpdate(rp);
			  continue;
		  }
		  RouterPacket rp = new RouterPacket(myID, x, nTables[myID]);
		  sendUpdate(rp);
    }
  }

}

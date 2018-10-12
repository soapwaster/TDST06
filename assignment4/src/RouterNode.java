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

  //--------------------------------------------------
  public RouterNode(int ID, RouterSimulator sim, int[] costs) {
    myID = ID;
    this.sim = sim;
    myGUI =new GuiTextArea("  Output window for Router #"+ ID + "  ");
    System.arraycopy(costs, 0, this.costs, 0, RouterSimulator.NUM_NODES);
    getNeighbors();
    nTables = new int[RouterSimulator.NUM_NODES][RouterSimulator.NUM_NODES];
    for(int i=0;i<nTables.length;i++) Arrays.fill(nTables[i], RouterSimulator.INFINITY);
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
    		List<Integer> min = new ArrayList<>();
        	for(int x : neighbors) {
        		min.add(costs[x]+nTables[x][i]);
        	}
        	int min_value = Collections.min(min);
        	/*if(myID == 2){
        		System.out.println("min_value : " + min_value);
            	System.out.println("nTables[" + myID + "]["+i+"]  : " + nTables[myID][i]);
        	}*/
        	System.out.println("min_value : "+min_value);
        	if(min_value != nTables[myID][i]){
        		flag = true;
        	}
        	nTables[myID][i] = min_value;
    	}
    }
    
    /*for(int i=0; i<pkt.mincost.length;i++){
    	int min = RouterSimulator.INFINITY;
    	int tmp = nTables[myID][i];
    	System.out.println(tmp);
    	 for (int x : neighbors) {
    		 if(costs[x] + nTables[x][i] < min){
    			 min = costs[x] + pkt.mincost[i];
    			 nTables[myID][i] = min;
    		 }
    	 }
    	 System.out.println(nTables[myID][i]);
    	 if(tmp != nTables[myID][i]) flag = true;
    }*/
    if(flag) broadcast();
    
    //System.out.println("myID = "+ myID +" ----> "+Arrays.toString(nTables[myID]));
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
	  /*System.out.println("("+myID+","+dest+") ---> "+newcost);
	  costs[dest] = newcost;
	  int[] fakeCost = costs.clone();
	  fakeCost[dest] = RouterSimulator.INFINITY;
	  for (int x : neighbors) {
			  sendUpdate(new RouterPacket(myID, x, fakeCost));
	  }
	  */
	  costs[dest] = newcost;
	  nTables[myID][dest] = newcost;
	  broadcast();
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
      RouterPacket rp = new RouterPacket(myID, x, nTables[myID]);
      sendUpdate(rp);
    }
  }

}

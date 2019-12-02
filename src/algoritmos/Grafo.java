
package algoritmos;

import java.awt.Color;

import java.awt.GridLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;





/**
 *
 * @author YaGaRa
 */
public class Grafo {

    HashMap<Integer, Vertice> Nodes = new HashMap<>();
    HashMap<Integer, Arista> Edges = new HashMap<>();
    HashMap<Integer, Arista> treeEdges = new HashMap<>();
    int loadedGraphDirected = 0;
    int dirigido = 0;
    int reachableNodes = 0;
    String WAVpath = "";
    boolean loadWAV = false;

    public Grafo() {
    }

    public int createNode(int id, String name, String data) {
        Vertice nodo = new Vertice(id, name, data);
        Nodes.put(id, nodo);
        return 0;
    }

    public int createNodeXY(int id, String name, double x, double y, String data) {
        Vertice nodo = new Vertice(id, name, x, y, data);
        Nodes.put(id, nodo);
        return 0;
    }

    public int createEdge(int id, Vertice a, Vertice b, String data, int dir) {
        Arista edge = new Arista(a, b, data);
        Edges.put(id, edge);
        a.updateGrad();
        b.updateGrad();
        if (dir == 0) {
            a.setAdjacent(b);
            b.setAdjacent(a);
        } else {
            a.setAdjacent(b);
        }
        return 0;
    }

    public int createTreeEdge(int id, Vertice a, Vertice b, String data) {
        Arista edge = new Arista(a, b, data);
        treeEdges.put(id, edge);
        return 0;
    }

    public int getGradNode(Vertice node) {
        return node.getGrad();
    }

    public void createNodes(int n) {
        int i;
        for (i = 0; i < n; i++) { // Crear n nodos
            createNode(i, "Node " + i, "No data");
        }
    }
    
    public void sieveNodes() {
        Set<Integer> reachable = new HashSet<>();
        Set set = Edges.entrySet();
        Iterator i = set.iterator();
        while(i.hasNext()) {
            Map.Entry mentry = (Map.Entry)i.next();
            Arista edge = (Arista)mentry.getValue();
            reachable.add(edge.getNodes().get(1).getId());
            reachable.add(edge.getNodes().get(2).getId());
        }
        this.reachableNodes = reachable.size();
    }
    
    public void createNodesFromFile(Set<Integer> nodesFromFile) {
        for (int index : nodesFromFile) {
            createNode(index, "Node " + index, "No data");
        }
    }

    public void createNodesXY(int n) {
        int i;
        double x,y;
        Random rand = new Random();
        for (i = 0; i <n; i++) { // Crea n nodos con coordenadas dentro de un cuadro de tamaño uno
            x = rand.nextFloat();
            y = rand.nextFloat();
            createNodeXY(i, "Node " + i, x, y, "No data");
        }
    }

    public Grafo Erdos(int n, int m, int dir, int cic) {
        this.Nodes.clear();
        this.Edges.clear();
        Random rand = new Random();
        int j = 0, indexA, indexB, create = 1;
        createNodes(n);
        
        while(Edges.size() < m) {
            indexA = rand.nextInt(n); // Elegir random nodo a
            indexB = rand.nextInt(n); // Elegir random nodo b
            if (cic == 0) { // ciclicos(autoc) no permitidos
                while (indexA == indexB) { // Ciclamos hasta que el nodo B difiere del nodo A
                    indexB = rand.nextInt(n);
                }
            }
            Vertice a = Nodes.get(indexA); // Recupera node A
            Vertice b = Nodes.get(indexB); // Recupera node B
            Set set = Edges.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) { // Iteramos sobre los bordes existentes
                Map.Entry mentry = (Map.Entry)iterator.next();
                mentry.getKey();
                Arista edge = (Arista)mentry.getValue();
                HashMap<Integer, Vertice> edgeNodes = edge.getNodes();
                Vertice A = edgeNodes.get(1); // Node a hasta arista
                Vertice B = edgeNodes.get(2); // Node b hasta arista
                create = 1;
                if ((a == A && b == B) || (a == B && b == A && dir == 0)) { //La arista ya existe!
                    create = 0; // si ya existe, entonces no se crea nuevamente
                    break;
                }
            }
            if (create != 0) {
                createEdge(j, a, b, "Connects node " + a.getId()+" and node " + b.getId(), dir); // Crea aristas
                create = 1; // resetea la condición
            }
            j++;
        }
        this.sieveNodes();
        return this;
    }

    public Grafo Gilbert(int n, double p, int dir, int cic) {
        this.Nodes.clear();
        this.Edges.clear();
        Random rand = new Random();
        int i, j, NE = 0, create = 1;
        double probability;
        createNodes(n);

        for(i = 0; i < n; i++) { // Repetimos mientras queden nodos
            for(j = 0; j < n; j++){
                if (cic == 0 && (i == j)) { // Ciclos no permitidos!
                    continue;
                }
                probability = rand.nextFloat(); // Crea random aleatorio
                if (probability > p) { // Crea un arsita
                    Vertice a = Nodes.get(i);
                    Vertice b = Nodes.get(j);
                    Set set = Edges.entrySet();
                    Iterator iterator = set.iterator();
                    while(iterator.hasNext()) { // Interamos mientras haya aristas
                        Map.Entry mentry = (Map.Entry)iterator.next();
                        mentry.getKey();
                        Arista edge = (Arista)mentry.getValue();
                        HashMap<Integer, Vertice> edgeNodes = edge.getNodes();
                        Vertice A = edgeNodes.get(1); // Nodo a hasta arista
                        Vertice B = edgeNodes.get(2); // Nodo b hasta arista
                        create = 1;
                        if ((a == A && b == B) || (a == B && b == A && dir == 0)) { // Arista ya existe?
                            create = 0; // De ser así no crea uno nuevamente
                            break;
                        }
                    }
                    if(create != 0) {
                        createEdge(NE, a, b, "Connects node " + a.getId() +" and node " + b.getId(), dir);
                        NE++;
                        create = 1;
                    }
                }
            }
        }
        probability = rand.nextFloat();
        if (probability > p) {
            Vertice a = Nodes.get(Nodes.size() - 1);
            Vertice b = Nodes.get(0);
            Set set = Edges.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) { // Iteramos mientras existan aristas
                Map.Entry mentry = (Map.Entry)iterator.next();
                mentry.getKey();
                Arista edge = (Arista)mentry.getValue();
                HashMap<Integer, Vertice> edgeNodes = edge.getNodes();
                Vertice A = edgeNodes.get(1); // Nodo a hasta arista
                Vertice B = edgeNodes.get(2); // Nodo b hasta arista
                create = 1;
                if ((a == A && b == B) || (a == B && b == A && dir == 0)) { // Arista ya existe?
                    create = 0; // De ser así no crea uno nuevo.
                    break;
                }
            }
            if(create != 0) {
                createEdge(NE, a, b, "Connects node " + a.getId() +" and node " + b.getId(), dir);
            }
        }
        this.sieveNodes();
        return this;
    }

    public Grafo Geo(int n, double r, int dir, int cic) {
        this.Nodes.clear();
        this.Edges.clear();
        int i, j, NE = 0, create = 1;
        createNodesXY(n);

        List index = new ArrayList();
        List jndex = new ArrayList();
        for (int k = 0; k < n; k++) {
            index.add(k);
            jndex.add(k);
        }
        Collections.shuffle(index);
        Collections.shuffle(jndex);
        int I, J;

        for(i = 0; i < n; i++) { // Repetimos mientras queden nodos
            I = (int)index.get(i);
            for(j = 0; j < n; j++){
                J = (int)jndex.get(j);
                if (cic == 0 && (I == J)) { // Cic no permitidos
                    continue;
                }
                Vertice a = Nodes.get(I);
                Vertice b = Nodes.get(J);
                double x1 = a.getX();
                double y1 = a.getY();
                double x2 = b.getX();
                double y2 = b.getY();
                double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
                if (d <= r) { // Crea un arista
                    Set set = Edges.entrySet();
                    Iterator iterator = set.iterator();
                    while(iterator.hasNext()) { // Iteramos mientras existan aristas.
                        Map.Entry mentry = (Map.Entry)iterator.next();
                        mentry.getKey();
                        Arista edge = (Arista)mentry.getValue();
                        HashMap<Integer, Vertice> edgeNodes = edge.getNodes();
                        Vertice A = edgeNodes.get(1); // Nodo a hasta arista
                        Vertice B = edgeNodes.get(2); // Nodo b hasta arista 
                        create = 1;
                        if ((a == A && b == B) || (a == B && b == A && dir == 0)) { // Arista ya existe?
                            create = 0; // De ser así entonces no crea uno nuevo.
                            break;
                        }
                    }
                    if(create != 0) {
                        createEdge(NE, a, b, "Connects node " + a.getId() +" and node " + b.getId(), dir);
                        NE++;
                        create = 1;
                    }
                }
            }
        }
        Vertice a = Nodes.get(Nodes.size() - 1);
        Vertice b = Nodes.get(0);
        double x1 = a.getX();
        double y1 = a.getY();
        double x2 = b.getX();
        double y2 = b.getY();
        double d = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
        if (d <= r) {
            Set set = Edges.entrySet();
            Iterator iterator = set.iterator();
            while(iterator.hasNext()) { // Iteramos mientras existan
                Map.Entry mentry = (Map.Entry)iterator.next();
                mentry.getKey();
                Arista edge = (Arista)mentry.getValue();
                HashMap<Integer, Vertice> edgeNodes = edge.getNodes();
                Vertice A = edgeNodes.get(1); // Nodo a hasta arista
                Vertice B = edgeNodes.get(2); // Nodo b hasta arista
                create = 1;
                if ((a == A && b == B) || (a == B && b == A && dir == 0)) { // Arista ya existe
                    create = 0; // De ser así no crea uno nuevo
                    break;
                }
            }
            if(create != 0) {
                createEdge(NE, a, b, "Connects node " + a.getId() +" and node " + b.getId(), dir);
            }
        }
        this.sieveNodes();
        return this;
    }

    public Grafo Barabasi(int n, int d, double g, int dir, int cic) {
        this.Nodes.clear();
        this.Edges.clear();
        int Ne = 0;
        double p = 0, pr;
        Random rand = new Random();
        for (int i = 0; i < n; i++) { // Itera D veces para crear N nodos
            createNode(i, "Node " + i, "No data"); // Crea A nodes
            for (int j = 0; j < Nodes.size() - (1 - cic); j++) { // Iterar sobre los nodos existentes para conectarlos
                // (1 - cic) Aquí se controla si se permite o no un auto-cic
                p = rand.nextFloat(); // Random probability
                pr = 1 - Nodes.get(j).getGrad() / (double)(g); // Valor proporcional al Gradiente del nodo
                if (i < d) {
                    pr = 1;
                }
                if (pr > p) {
                    Vertice A = Nodes.get(Nodes.size() - 1);
                    Vertice B = Nodes.get(j);
                    createEdge(Ne, A, B, "Connects node " + A.getId() + " and node " + B.getId(), dir);
                    Ne++;
                }
            }
        }
        this.sieveNodes();
        return this;
    }

    public HashMap<Integer, Vertice> getNodesGraph() {
        return Nodes;
    }

    public HashMap<Integer, Arista> getEdgesGraph() {
        return Edges;
    }

    public int graphGraph(Grafo grapho, int dir, String name) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(name));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            name = fc.getCurrentDirectory().toString() + "\\" + fc.getSelectedFile().getName();
            HashMap<Integer, Arista> EG = grapho.getEdgesGraph();
            try (FileWriter fw = new FileWriter(name+".gv");
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                if (dir == 0) { 
                    out.println("strict graph{");
                    out.flush();
                } else { 
                    out.println("strict digraph{");
                    out.flush();
                }

            Set setE = EG.entrySet();
            Iterator it = setE.iterator();
            while(it.hasNext()) {
                Map.Entry mentry = (Map.Entry)it.next();
                Arista edge = (Arista)mentry.getValue();
                HashMap<Integer, Vertice> anode = edge.getNodes();
                Vertice A = anode.get(1);
                Vertice B = anode.get(2);
                if (dir == 0) { 
                    out.println("   \"" + A.getId() + "\"--\"" + B.getId() + "\"");
                    out.flush();
                } else { 
                    out.println("   \"" + A.getId() + "\"->\"" + B.getId() + "\"");
                    out.flush();
                }
            }
            out.println("}");
            out.close();
            JOptionPane.showMessageDialog(null, "El grafo se guarda en: " + name + ".gv", "Grafo Creado", JOptionPane.INFORMATION_MESSAGE);
            return 1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "El grafo no fue guardado en : " + name + ".gv", "Grafo NO creado...", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Grafo no guardado.", "Grafo NO creado...", JOptionPane.INFORMATION_MESSAGE);
        }
        return 1;
    }

    public HashMap<Integer, Arista> getEdgesTree() {
        return treeEdges;
    }

    public int graphTree(Grafo grafo, String name) {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(name));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            name = fc.getCurrentDirectory().toString() + "\\" + fc.getSelectedFile().getName();
            HashMap<Integer, Arista> EG = grafo.getEdgesTree();
            try (FileWriter fw = new FileWriter(name+".gv");
                BufferedWriter bw = new BufferedWriter(fw);
                PrintWriter out = new PrintWriter(bw)) {
                out.println("strict graph{");
                out.flush();

                Set setE = EG.entrySet();
                Iterator it = setE.iterator();
                while(it.hasNext()) {
                    Map.Entry mentry = (Map.Entry)it.next();
                    Arista edge = (Arista)mentry.getValue();
                    HashMap<Integer, Vertice> anode = edge.getNodes();
                    Vertice A = anode.get(1);
                    Vertice B = anode.get(2);
                    out.println("   \"" + A.getId() + "\"--\"" + B.getId() + "\"");
                    out.flush();
                }
                out.println("}");
                out.close();
                JOptionPane.showMessageDialog(null, "El grafo se guardará en: " + name + ".gv", "Graph Created", JOptionPane.INFORMATION_MESSAGE);
                return 1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "El grafo NO se guardó en: " + name + ".gv", "Graph Not Created", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Grafo no guardado.", "Intenta de nuevo...", JOptionPane.INFORMATION_MESSAGE);
        }
        return 1;
    }

    boolean flag = true;
    String path_n="";
    public String readGraph(Grafo grafo) {
        flag = true;
        Set<Integer> nodesFromFile = new HashSet<>();
        grafo.Nodes.clear();
        grafo.Edges.clear();
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File(System.getProperty("user.home")));
        JFrame f = new JFrame("");
        JPanel p = new JPanel(new GridLayout(0,1));
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            SwingWorker<Void, Void> worker;
            final JDialog dialog = new JDialog(f, true);
            dialog.setUndecorated(true);
            dialog.setLocationRelativeTo(null);
            dialog.setLocation(f.getLocation().x + f.getSize().width / 4, f.getLocation().y + f.getSize().height / 4);
            JProgressBar bar = new JProgressBar();
            bar.setIndeterminate(true);
            bar.setStringPainted(true);
            bar.setBackground(Color.green);
            bar.setString("Loading. Please wait...");
            dialog.add(bar);
            dialog.pack();
            worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    int dir = 0;
                    int edges = 0;
                    int nodes = 0;
                    File selectedFile = fc.getSelectedFile();
                    Path path = Paths.get(selectedFile.getAbsolutePath());
                    path_n= String.valueOf(path.getFileName());
                    List<String> lines = Collections.emptyList();
                    try {
                        lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                        for (String line : lines) {
                            if (line.contains("--") || line.contains("->")) {
                                String[] currentLine = {""};
                                if (line.contains("--")) {
                                    dir = 0;
                                    grafo.dirigido = 0;
                                    currentLine = line.replace("\"", "").trim().split("--");
                                }
                                if (line.contains("->")) {
                                    dir = 1;
                                    grafo.dirigido = 1;
                                    currentLine = line.replace("\"", "").trim().split("->");
                                }
                                String[] nodeL = currentLine[0].split(",| ");
                                String[] nodeR = currentLine[1].split(",| ");
                                int izq = Integer.parseInt(nodeL[0]);
                                int der = Integer.parseInt(nodeR[0]);
                                nodesFromFile.add(izq);
                                nodesFromFile.add(der);
                            }
                        }
                        createNodesFromFile(nodesFromFile);
                        grafo.reachableNodes = nodesFromFile.size();
                        for (String line : lines) {
                            if(line.contains("--") || line.contains("->")) {
                                String[] currentLine = {""};
                                if (line.contains("--")) {
                                    dir = 0;
                                    grafo.dirigido = 0;
                                    currentLine = line.replace("\"", "").trim().split("--");
                                }
                                if (line.contains("->")) {
                                    dir = 1;
                                    grafo.dirigido = 1;
                                    currentLine = line.replace("\"", "").trim().split("->");
                                }
                                String[] nodeL = currentLine[0].split(",| ");
                                String[] nodeR = currentLine[1].split(",| ");
                                int izq = Integer.parseInt(nodeL[0]);
                                int der = Integer.parseInt(nodeR[0]);
                                createEdge(edges, grafo.Nodes.get(izq), grafo.Nodes.get(der), "Connects node " + izq + " and node " + der, dir);
                                edges++;
                            }
                        }
                        for(String line : lines) {
                            if(line.contains("label")) {
                                String[] currentLine = {""};
                                if(line.contains("--")) {
                                    dir = 0;
                                    currentLine = line.replace("\"", "").trim().split("--|=");
                                }
                                if(line.contains("->")) {
                                    dir = 1;
                                    currentLine = line.replace("\"", "").trim().split("->|=");
                                }
                                String[] nodeL = currentLine[0].split(",| ");
                                int izq = Integer.parseInt(nodeL[0]);
                                String[] nodeR = currentLine[1].split(",| ");
                                int der = Integer.parseInt(nodeR[0]);
                                String[] weight = currentLine[2].trim().split(",| |\\[|\\]");
                                double weigh = Double.parseDouble(weight[0]);
                                Arista edge = new Arista(grafo.Nodes.get(izq), grafo.Nodes.get(der), "Connects node " + izq + " and node " + der, dir);
                                Set set = grafo.Edges.entrySet();
                                Iterator it = set.iterator();
                                while(it.hasNext()) {
                                    Map.Entry mentry = (Map.Entry)it.next();
                                    Arista edgeCompara = (Arista)mentry.getValue();
                                    if(edge.equals(edgeCompara)) {
                                        edgeCompara.setWeight(weigh);
                                    }
                                }
                            }
                        }
                    } catch(IOException e) {
                        JOptionPane.showMessageDialog(null, "The file could not be loaded.", "File not loaded", JOptionPane.INFORMATION_MESSAGE);
                        flag = false;
                        return null;
                    }
                    return null;
                }
                @Override
                protected void done() {
                    dialog.dispose();
                }
            };
            worker.execute();
            dialog.setVisible(true);
            if (flag) {
                System.out.println(path_n);
                return path_n;
            }
            return null;
        }
        return null;
    }

    public void resetNodes(Grafo grafo) {
        Set set = grafo.getNodesGraph().entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            Vertice node = (Vertice)mentry.getValue();
            node.visited = false;
        }
    }

    public void resetTree(Grafo grafo) {
        grafo.treeEdges.clear();
    }
    
    public void BFS(Grafo grafo, Vertice root) {
        Deque<Vertice> myQ = new LinkedList<>();
        myQ.add(root);
        int adding = 0;
        while(!myQ.isEmpty()) {
            Vertice current = myQ.pollFirst();
            current.visited = true;
            HashMap<Integer, Vertice> neighbors = current.getAdjacentNodes();
            Set set = neighbors.entrySet();
            Iterator it = set.iterator();
            while(it.hasNext()) {
                Map.Entry mentry = (Map.Entry)it.next();
                Vertice neighbor = (Vertice)mentry.getValue();
                if (!neighbor.visited) {
                    neighbor.visited = true;
                    myQ.addLast(neighbor);
                    createTreeEdge(adding, current, neighbor, "");
                    adding++;
                }
            }
        }
    }

    public int DFS_R(Grafo grafo, Vertice root) {
        HashMap<Integer, Vertice> neighbors = root.getAdjacentNodes();
        root.visited = true;
        Set set = neighbors.entrySet();
        Iterator it = set.iterator();
        int id = -1;
        Vertice neighbor = new Vertice();
        while (it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            neighbor = (Vertice)mentry.getValue();
            if (neighbor != null && !neighbor.visited) {
                id = DFS_R(grafo, neighbor);
                if (id != root.getId()) {
                    createTreeEdge(id, root, grafo.getNodesGraph().get(id), "");
                }
            }
        }
        return root.getId();
    }

    public void DFS_I(Grafo grafo, Vertice root){
        Stack<Vertice> stack = new Stack<>();
        Deque<Vertice> nodosIndex = new LinkedList<>();
        stack.add(root);
        root.visited = true;
        int adding = 0;
        while (!stack.isEmpty()) {
            Vertice current = stack.pop();
            nodosIndex.add(current);
            HashMap<Integer, Vertice> neighbors = current.getAdjacentNodes();
            Set set = neighbors.entrySet();
            Iterator it = set.iterator();
            while (it.hasNext()) {
                Map.Entry mentry = (Map.Entry)it.next();
                Vertice neighbor = (Vertice)mentry.getValue();
                if (neighbor != null && !neighbor.visited) {
                    stack.add(neighbor);
                    neighbor.visited = true;
                }
            }
        }
        Deque<Vertice> reverseDeque = new LinkedList<>();
        while (!nodosIndex.isEmpty()) {
            Vertice current = nodosIndex.poll();
            //reverseDeque.add(current);
            reverseDeque.addFirst(current);
            if (!nodosIndex.isEmpty()){
                if (current.adjacentNodes.containsValue(nodosIndex.peek())) {
                    //System.out.println(current.getId()+"->"+nodosIndex.peek().getId());
                    createTreeEdge(adding, current, nodosIndex.peek(), "");
                    adding++;
                } else {
                    Iterator it = reverseDeque.iterator();
                    it.next();
                    while (it.hasNext()) {
                        Vertice previousNode = (Vertice)it.next();
                        if (previousNode.adjacentNodes.containsValue(nodosIndex.peek())) {
                            //System.out.println(previousNode.getId()+"->"+nodosIndex.peek().getId());
                            createTreeEdge(adding, previousNode, nodosIndex.peek(), "");
                            adding++;
                            break;
                        }
                    }
                }
            }
        }
    }
    
     public void setEdgeWeights(double min, double max, boolean onlyInteger) {
        Set set = Edges.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            Arista edge = (Arista)mentry.getValue();
            if(onlyInteger) {
                edge.weight = min + (int)(Math.random() * ((max - min) + 1));
            } else {
                edge.weight = min + (Math.random() * ((max - min) + 1));
            }
        }
        graphWeightedGraph();
    }

    /**
     * It saves to a file the weighted graph.
     * @return 1 if everything was fine. 0 otherwise.
     */
   
     
    public int graphWeightedGraph() {
        //System.out.println("The graph will be saved in: C:\\temp\\"+name+".gv");
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            String name = fc.getCurrentDirectory().toString() + "\\" + fc.getSelectedFile().getName();
            try (FileWriter fw = new FileWriter(name+".gv");
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                if (dirigido == 0) { // Not directed graph
                    out.println("strict graph{");
                    out.flush();
                } else { // Directed graph
                    out.println("strict digraph{");
                    out.flush();
                }

            Set setE = Edges.entrySet();
            Iterator it = setE.iterator();
            while(it.hasNext()) {
                Map.Entry mentry = (Map.Entry)it.next();
                Arista edge = (Arista)mentry.getValue();
                HashMap<Integer, Vertice> anode = edge.getNodes();
                Vertice A = anode.get(1);
                Vertice B = anode.get(2);
                if (dirigido == 0) { // Not directed graph
                    out.println("   \"" + A.getId() + "\"--\"" + B.getId() + "\" [label = \"" + edge.getWeight() + "\"]");
                    out.flush();
                } else { // Directed graph
                    out.println("   \"" + A.getId() + "\"->\"" + B.getId() + "\" [label = \"" + edge.getWeight() + "\"]");
                    out.flush();
                }
            }
            out.println("}");
            out.close();
            JOptionPane.showMessageDialog(null, "Grafo se guardara en: " + name + ".gv", "Grafo guardado", JOptionPane.INFORMATION_MESSAGE);
            return 1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "El grafo no se guardo en: " + name + ".gv", "Grafo no creado", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Grafo No guardado.", "Grafo no creado", JOptionPane.INFORMATION_MESSAGE);
        }
        return 1;
    }
    
    public void Dijkstra(Grafo grafo, Vertice s) {
        int key = -1;
        Arista edge = new Arista();
        Arista edgeO = new Arista();
        Set<Integer> settled = new HashSet<>();
        PriorityQueue<Vertice> pq = new PriorityQueue<>();
        // Set source node dist as 0
        // HashMap for keeping track of predecessors
        HashMap<Integer, Integer> prev = new HashMap<>();
        prev.put(s.getId(), Integer.MIN_VALUE);
        // HashMap for keeping track of distances
        HashMap<Integer, Double> dist = new HashMap<>();
        dist.put(s.getId(), 0.0);
        Set set = Nodes.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            Vertice nodo = (Vertice)mentry.getValue();
            // Set nodes not source cost as infinite
            if (nodo.getId() != s.getId()) {
                dist.put(nodo.getId(), Double.MAX_VALUE);
            }
        }
        pq.add(s);
        //while(settled.size() != Nodes.size()) { //No jala esta condición (???)
        while(!pq.isEmpty()) {
            Vertice u = pq.remove();
            settled.add(u.getId());
            double edgeDistance = -1.0;
            double newDistance = -1.0;
            Set set1 = u.adjacentNodes.entrySet();
            //set1.removeAll(pq);
            Iterator ite = set1.iterator();
            while(ite.hasNext()) {
                Map.Entry mentry = (Map.Entry)ite.next();
                Vertice v = (Vertice)mentry.getValue();
                //System.out.println(prev.containsKey(v.getId()));
                //System.out.print(v.getId());
                if(!settled.contains(v.getId())) {
                    edge = new Arista(u, v, "Connects node " + u.getId() + " and node " + v.getId());
                    edgeO = new Arista(v, u, "Connects node " + v.getId() + " and node " + u.getId());
                    Set setedge = Edges.entrySet();
                    Iterator iteratorEdge = setedge.iterator();
                    while(iteratorEdge.hasNext()) {
                        Map.Entry mentryEdge = (Map.Entry)iteratorEdge.next();
                        Arista edgeCompara = (Arista)mentryEdge.getValue();
                        if (edge.equals(edgeCompara)) {
                            key = (int)mentryEdge.getKey();
                            edgeDistance = edgeCompara.getWeight();
                            break;
                        }
                        if (edgeO.equals(edgeCompara)) {
                            key = (int)mentryEdge.getKey();
                            edgeDistance = edgeCompara.getWeight();
                            break;
                        }
                    }
                    //edgeDistance = v.getCost();
                    newDistance = dist.get(u.getId()) + edgeDistance;
                    if(newDistance <= dist.get(v.getId())) {
                        dist.put(v.getId(), newDistance);
                        v.setCost(newDistance);
                        prev.put(v.getId(), u.getId());
                    }
                    pq.add(v);
                }
            }
        }
        graphDijkstra(grafo, "Dijkstra", prev, dist);
     
    }
    
    public int graphDijkstra(Grafo grafo, String name, HashMap<Integer, Integer> prev, HashMap<Integer, Double> dist) {
        //System.out.println("The graph will be saved in: C:\\temp\\"+name+".gv");
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(name));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            name = fc.getCurrentDirectory().toString() + "\\" + fc.getSelectedFile().getName();
            HashMap<Integer, Arista> EG = grafo.getEdgesGraph();
            try (FileWriter fw = new FileWriter(name+".gv");
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                if (dirigido == 0) { // Not directed graph
                    out.println("strict graph{");
                    out.flush();
                } else { // Directed graph
                    out.println("strict digraph{");
                    out.flush();
                }
            Set setPrev = prev.entrySet();
            Iterator itPrev = setPrev.iterator();
            while(itPrev.hasNext()) {
                Map.Entry mentry = (Map.Entry)itPrev.next();
                int hijo = (int)mentry.getKey();
                int padre = (int)mentry.getValue();
                if (Integer.MIN_VALUE == padre) {
                    continue;
                }
                Arista edge = new Arista(Nodes.get(padre), Nodes.get(hijo), "Connects node " + padre + " and node " + hijo);
                Arista edgeO = new Arista(Nodes.get(hijo), Nodes.get(padre), "Connects node " + hijo + " and node " + padre);
                //System.out.println(padre+":"+hijo);
                double edgeWeight = 0.0;
                Set setE = EG.entrySet();
                Iterator it = setE.iterator();
                while(it.hasNext()) {
                    Map.Entry entryEdge = (Map.Entry)it.next();
                    Arista enGrafo = (Arista)entryEdge.getValue();
                    if(edge.equals(enGrafo)) {
                        edgeWeight = enGrafo.getWeight();
                        break;
                    }
                    if(edgeO.equals(enGrafo)) {
                        edgeWeight = enGrafo.getWeight();
                        break;
                    }
                }
                if (dirigido == 0) { // Not directed graph
                    out.println("   \"" + padre + ",(" + dist.get(padre) + ")\"--\"" + hijo + ",(" + dist.get(hijo) + ")\" [label = \"" + edgeWeight + "\"]");
                    out.flush();
                } else { // Directed graph
                    out.println("   \"" + padre + ",(" + dist.get(padre) + ")\"->\"" + hijo + ",(" + dist.get(hijo) + ")\" [label = \"" + edgeWeight + "\"]");
                    out.flush();
                }
            }
            out.println("}");
            out.close();
            JOptionPane.showMessageDialog(null, "El grafo se guardara en: " + name + ".gv", "grafo creado", JOptionPane.INFORMATION_MESSAGE);
            return 1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "el grafo no se guardo en: " + name + ".gv", "grafo NO guardado", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Grafo no Guardado.", "Grafo no creado", JOptionPane.INFORMATION_MESSAGE);
        }
        return 1;
    }
    
     public void kruskal(Grafo grafo) {
        PriorityQueue<Arista> pq = new PriorityQueue<>();
        HashMap<Integer, Integer> prev = new HashMap<>();
        HashMap<Integer, Arista> mst = new HashMap<>();
        Set set = Edges.entrySet();
        Iterator it = set.iterator();
        // Add all edges to priority queue
        while (it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            Arista edge = (Arista)mentry.getValue();
            pq.add(edge);
        }
        // Create list of parents for each node. Initially each nodes is its parent
        Set nodeSet = Nodes.entrySet();
        Iterator nodesIterator = nodeSet.iterator();
        while(nodesIterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)nodesIterator.next();
            Vertice node = (Vertice)mentry.getValue();
            prev.put(node.getId(), node.getId());
        }
        // Process nodes-1 edges
        int index = 0;
        //while(index < grafo.reachableNodes - 1) {
        while(!pq.isEmpty()) {
            Arista edge = pq.poll();
            // Check if adding this edge creates a cycle
            int first = find(prev, edge.a.getId());
            int second = find(prev, edge.b.getId());
            if (first == second) {
                // We ignore since it would create a cycle
                //System.out.println("No avanzo weeee" + grafo.reachableNodes+" : "+mst.size());
                Set zet = mst.entrySet();
                Iterator ite = zet.iterator();
                while(ite.hasNext()) {
                    Map.Entry menty = (Map.Entry)ite.next();
                    Arista edje = (Arista)menty.getValue();
                    //System.out.println(edje.toString());
                }
            } else {
                // Add it to our final result
                mst.put(index, edge);
                index++;
                union(prev, first, second);
            }
        }
        // Print MST
        this.graphMST(grafo, "MST-Kruskal", mst);
        this.resetNodes(grafo);
       
    }
     
    public int find(HashMap<Integer, Integer> prev, int idNode) {
        // Chain of parent pointers from x upwards through the tree
        // until an element is reached whose parent is itself
        if(prev.get(idNode) != idNode) {
            //System.out.println("Stuck_find");
            return find(prev, prev.get(idNode));
        }
        return idNode;
    }
    
     public void union(HashMap<Integer, Integer> prev, int x, int y) {
        int x_parent = find(prev, x);
        int y_parent = find(prev, y);
        // Make x as parent of y
        //System.out.println("Stuck_Union");
        prev.put(y_parent, x_parent);
    }
     
    public boolean inverseKruskal(Grafo grafo, Vertice s) {
        grafo.DFS_R(grafo, s);
        if(!grafo.checkAllVisited()){
            JOptionPane.showMessageDialog(null, "This method does not support not connected graphs.", "Error.", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        PriorityQueue<Arista> pq = new PriorityQueue<>(Collections.reverseOrder());
        HashMap<Integer, Arista> mst = new HashMap<>();
        Set set = Edges.entrySet();
        Iterator it = set.iterator();
        // Add all edges to priority queue
        while (it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            Arista edge = (Arista)mentry.getValue();
            pq.add(edge);
        }
        int index = 0;
        while(!pq.isEmpty()) {
            grafo.resetNodes(grafo);
            Arista edge = pq.poll();
            Vertice a = edge.a;
            Vertice b = edge.b;
            b.adjacentNodes.remove(a.getId());
            a.adjacentNodes.remove(b.getId());
            // Check if removing the edge disconnects the graph
            grafo.DFS_R(grafo, a);
            boolean test = checkAllVisited();
            if (test) {
            } else {
                // Add edge to our final result
                mst.put(index, edge);
                index++;
                b.adjacentNodes.put(a.getId(), a);
                a.adjacentNodes.put(b.getId(), b);
            }
        }
        // Print MST
        this.graphMST(grafo, "MST-Kruskal_Inverso", mst);
        this.resetNodes(grafo);
        return true;
    } 
    
    public boolean prim(Grafo grafo, Vertice s) {
        grafo.BFS(grafo, s);
        if(!grafo.checkAllVisited()){
            JOptionPane.showMessageDialog(null, "This method does not support not connected graphs.", "Error.", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        HashMap<Integer, Arista> mst = new HashMap<>();
        int index = 0;
        Arista edge = new Arista();
        Arista edgeO = new Arista();
        Set<Integer> settled = new HashSet<>();
        PriorityQueue<Vertice> pq = new PriorityQueue<>();
        // Set source node dist as 0
        // HashMap for keeping track of predecessors
        HashMap<Integer, Integer> prev = new HashMap<>();
        prev.put(s.getId(), Integer.MIN_VALUE);
        // HashMap for keeping track of distances
        HashMap<Integer, Double> dist = new HashMap<>();
        dist.put(s.getId(), 0.0);
        Set set = Nodes.entrySet();
        Iterator it = set.iterator();
        while(it.hasNext()) {
            Map.Entry mentry = (Map.Entry)it.next();
            Vertice nodo = (Vertice)mentry.getValue();
            if (nodo.getId() != s.getId()) {
                dist.put(nodo.getId(), Double.MAX_VALUE);
            }
        }
        pq.add(s);
        while(!pq.isEmpty()) {
            Vertice u = pq.remove();
            settled.add(u.getId());
            double edgeDistance = -1.0;
            Set set1 = u.adjacentNodes.entrySet();
            Iterator ite = set1.iterator();
            while(ite.hasNext()) {
                Arista edgeCompara = new Arista();
                Map.Entry mentry = (Map.Entry)ite.next();
                Vertice v = (Vertice)mentry.getValue();
                if(!settled.contains(v.getId())) {
                    edge = new Arista(u, v, "Connects node " + u.getId() + " and node " + v.getId());
                    edgeO = new Arista(v, u, "Connects node " + v.getId() + " and node " + u.getId());
                    Set setedge = Edges.entrySet();
                    Iterator iteratorEdge = setedge.iterator();
                    while(iteratorEdge.hasNext()) {
                        Map.Entry mentryEdge = (Map.Entry)iteratorEdge.next();
                        edgeCompara = (Arista)mentryEdge.getValue();
                        if (edge.equals(edgeCompara) || edgeO.equals(edgeCompara)) {
                            edgeDistance = edgeCompara.getWeight();
                            break;
                        }
                    }
                    if(edgeDistance < dist.get(v.getId())) {
                        dist.put(v.getId(), edgeDistance);
                        v.setCost(edgeDistance);
                        prev.put(v.getId(), u.getId());
                        mst.put(v.getId(), edgeCompara);
                        index++;
                    }
                    pq.add(v);
                }
            }
        }
        this.graphMST(grafo, "MST-Prim", mst);
        this.resetNodes(grafo);
       
        return true;
    }
    
    public boolean checkAllVisited() {
        Set set = Nodes.entrySet();
        Iterator ite = set.iterator();
        while(ite.hasNext()) {
            Map.Entry mentry = (Map.Entry)ite.next();
            Vertice node = (Vertice)mentry.getValue();
            if(node.visited) {
            } else {
                return false;
            }
        }
        this.resetNodes(this);
        return true;
    }
     
    public int graphMST(Grafo grafo, String name, HashMap<Integer, Arista> mst) {
        //System.out.println("The graph will be saved in: C:\\temp\\"+name+".gv");
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File(name));
        if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            name = fc.getCurrentDirectory().toString() + "\\" + fc.getSelectedFile().getName();
            try (FileWriter fw = new FileWriter(name+".gv");
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter out = new PrintWriter(bw)) {
                if (dirigido == 0) { // Not directed graph
                    out.println("strict graph{");
                    out.flush();
                } else { // Directed graph
                    out.println("strict digraph{");
                    out.flush();
                }
            Set setPrev = mst.entrySet();
            Iterator itPrev = setPrev.iterator();
            double weight = 0.0;
            while(itPrev.hasNext()) {
                Map.Entry mentry = (Map.Entry)itPrev.next();
                Arista edge = (Arista)mentry.getValue();
                if (dirigido == 0) { // Not directed graph
                    weight += edge.getWeight();
                    out.println("   \"" + edge.a.getId() + "\"--\"" + edge.b.getId() + "\" [label = \"" + edge.getWeight() + "\"]");
                    out.flush();
                } else { // Directed graph
                    weight += edge.getWeight();
                    out.println("   \"" + edge.a.getId() + "\"->\"" + edge.b.getId() + "\" [label = \"" + edge.getWeight() + "\"]");
                    out.flush();
                }
            }
            out.println("labelloc=\"t\"\nlabel=\"MST's weight="+weight+"\"");
            out.println("}");
            out.close();
            JOptionPane.showMessageDialog(null, "El grafo se guardara en: " + name + ".gv", "grafo creado", JOptionPane.INFORMATION_MESSAGE);
            return 1;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "El grafo no fue guardado en: " + name + ".gv", "grafo NO creado", JOptionPane.INFORMATION_MESSAGE);
                return 0;
            }
        } else {
            JOptionPane.showMessageDialog(null, "Grafo no Guardado.", "Grafo no creado.", JOptionPane.INFORMATION_MESSAGE);
        }
        return 1;
    } 
    
}
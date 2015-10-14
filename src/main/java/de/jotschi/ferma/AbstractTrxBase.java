package de.jotschi.ferma;

import com.syncleus.ferma.FramedGraph;

import de.jotschi.ferma.impl.OrientDBTrxFactory;

/**
 * An abstract base class that can be used to implement database specific Trx and NoTrx classes.
 * 
 * @param <T>
 */
public class AbstractTrxBase<T extends FramedGraph> {



	/**
	 * Any graph that was found within the thread local is stored here while the new graph is executing. This graph must be restored when the autoclosable.
	 * closes.
	 */
	private FramedGraph oldGraph;

	/**
	 * Graph that is active within the scope of the autoclosable.
	 */
	private T currentGraph;

	/**
	 * Initialize the transaction.
	 * 
	 * @param transactionalGraph
	 */
	protected void init(T transactionalGraph) {
		// 1. Set the new transactional graph so that it can be accessed via Trx.getGraph()
		setGraph(transactionalGraph);
		// Handle graph multithreading issues by storing the old graph instance that was found in the threadlocal in a field.
		setOldGraph(OrientDBTrxFactory.getThreadLocalGraph());
		// Overwrite the current active threadlocal graph with the given transactional graph. This way Ferma graph elements will utilize this instance.
		OrientDBTrxFactory.setThreadLocalGraph(transactionalGraph);
	}

	public T getGraph() {
		return currentGraph;
	}

	protected void setGraph(T currentGraph) {
		this.currentGraph = currentGraph;
	}

	protected void setOldGraph(FramedGraph oldGraph) {
		this.oldGraph = oldGraph;
	}

	protected FramedGraph getOldGraph() {
		return oldGraph;
	}

}

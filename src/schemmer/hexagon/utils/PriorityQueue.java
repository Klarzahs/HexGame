package schemmer.hexagon.utils;

import schemmer.hexagon.map.Hexagon;

public class PriorityQueue {
	public Hexagon hex;
	public PriorityQueue next;
	public PriorityQueue before;
	public int prio;
	
	public PriorityQueue(Hexagon h, int prio){
		hex = h;
		this.prio = prio;
	}
	
	public PriorityQueue(){
	}
	
	public void add(Hexagon h, int p){
		if(!this.isEmpty()){
			if(this.prio <= p){
				if(next == null){
					next = new PriorityQueue(h, p);
					next.before = this;
				}else{
					next.add(h, p);
				}
			}
			else{	// p > prio
				if(next == null){
					this.next = new PriorityQueue(this.hex, this.prio);
					this.hex = h;
					this.prio = p;
					this.next.before = this;
				}else{
					PriorityQueue pnew = new PriorityQueue(this.hex, this.prio);
					this.hex = h;
					this.prio = p;
					pnew.next = this.next;
					pnew.next.before = pnew;
					this.next = pnew;
					pnew.before = this;
				}
			}
		} else{
			this.prio = p;
			this.hex = h;
		}
	}
	
	public boolean delete(int p){
		if(this.prio == p){
			if(this.before != null){
				this.before.next = this.next;
				if(this.next != null)
					this.next.before = this.before;
			} else {
				this.deleteFirst();
			}
			return true;
		}else{
			if(next != null)
				return next.delete(p);
			return false;
		}
	}
	
	public Hexagon getFirst(){
		return this.hex;
	}
	
	public boolean delete(Hexagon h){
		if(this.hex == h){
			if(this.before != null){
				this.before.next = this.next;
				if(this.next != null)
					this.next.before = this.before;
			} else {
				this.deleteFirst();
			}
			return true;
		}else{
			if(next != null)
				return next.delete(h);
			return false;
		}
	}
	
	public void deleteFirst(){
		if(this.next != null){
			this.hex = next.hex;
			this.prio = next.prio;
			this.next.before = null;
			this.next = next.next;
		} else{
			this.prio = 0;
			this.hex = null;
		}
	}
	
	public int getFirstPriority(){
		return this.prio;
	}
	
	public String toString(){
		if(next == null)
			return ""+this.prio;
		return this.prio+", "+next.toString();
	}
	
	public boolean contains(Hexagon h){
		if(isEmpty())
			return false;
		if(this.hex == h)
			return true;
		if(this.next == null)
			return false;
		return next.contains(h);
	}
	
	public boolean isEmpty(){
		return (this.hex == null);
	}
}

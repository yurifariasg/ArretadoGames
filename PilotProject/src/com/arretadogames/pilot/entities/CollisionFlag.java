package com.arretadogames.pilot.entities;

/**
 * Usado de maneira independente para setar a categoryBits e a maskbits de filtros de colisao
 * por default os filtros sao criados com categoryBits 1 e maskBits para colidir com todos 
 * os outros
 * @author danilopmn
 *
 */
public enum CollisionFlag {
	GROUP_1(1),//default, usado por todas as entidades que querem se colidir no jogo, players, chao 
	GROUP_2(2),//usado pelo buraco do tatu e o tatu
	GROUP_3(4);//usado por quem nao deseja se colidir, senario e uma fixture do fogo
	
	private int value;
	CollisionFlag( int a){
		value = a;
	}
	
	public int getValue(){
		return value;
	}
}

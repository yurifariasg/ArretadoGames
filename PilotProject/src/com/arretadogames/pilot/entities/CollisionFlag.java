package com.arretadogames.pilot.entities;

/**
 * Usado de maneira independente para setar a categoryBits e a maskbits de filtros de colisao
 * por default os filtros sao criados com categoryBits 1 e maskBits para colidir com todos 
 * os outros
 * @author danilopmn
 *
 */
public enum CollisionFlag {
	GROUP_COMMON_ENTITIES(1),//default, usado por todas as entidades que querem se colidir no jogo, players, chao 
    GROUP_PLAYERS(2), // usado pelos jogadores
	GROUP_TATU_HOLE(4),//usado pelo buraco do tatu e o tatu
	GROUP_NON_COLLIDABLE(8),//usado por quem nao deseja se colidir, senario e uma fixture do fogo
    GROUP_GROUND(16);
	
	private int value;
	CollisionFlag(int a){
		value = a;
	}
	
	public int getValue(){
		return value;
	}
}

package io.agentscope.examples.fightlandlord.util;

import com.google.api.client.util.Lists;
import io.agentscope.core.ReActAgent;
import io.agentscope.examples.fightlandlord.entity.Player;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Allen Hu
 * @date 2026/1/20
 */
public class IndexTest {

    @Test
    public void test() {
        List<Player> players = Lists.newArrayList();
        players.add(Player.builder().agent(ReActAgent.builder().build()).name("1").build());
        players.add(Player.builder().agent(ReActAgent.builder().build()).name("2").build());
        players.add(Player.builder().agent(ReActAgent.builder().build()).name("3").build());

        int firstBidderIndex = 1;

        LinkedBlockingQueue<Player> secondPlayers = new LinkedBlockingQueue<>();
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get((firstBidderIndex + i) % players.size());
            secondPlayers.add(player);
        }

        System.out.println(secondPlayers);
    }
}

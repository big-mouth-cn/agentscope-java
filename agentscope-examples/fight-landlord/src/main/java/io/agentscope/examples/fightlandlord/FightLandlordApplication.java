/*
 * Copyright 2024-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.agentscope.examples.fightlandlord;

/**
 * 斗地主游戏主程序入口。
 */
public class FightLandlordApplication {

    public static void main(String[] args) {
        try {
            FightLandlordGame game = new FightLandlordGame();
            game.start();
        } catch (Exception e) {
            System.err.println("游戏运行出错：" + e.getMessage());
            e.printStackTrace();
        }
    }
}

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
package io.agentscope.examples.fightlandlord.model;

import java.util.List;

/**
 * 出牌的结构化输出模型。
 */
public class PlayCardModel {
    /** 是否出牌：true表示出牌，false表示过牌 */
    public Boolean willPlay;
    /** 要出的牌（用字符串表示，如 "♠3,♥5,♦K" 或 "小王,大王"） */
    public List<String> cards;
    /**
     * 决策原因
     */
    public String reason;

    public PlayCardModel() {}
}

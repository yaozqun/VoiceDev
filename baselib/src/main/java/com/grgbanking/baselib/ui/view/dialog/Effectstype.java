package com.grgbanking.baselib.ui.view.dialog;

import com.grgbanking.baselib.ui.view.dialog.effects.BaseEffects;
import com.grgbanking.baselib.ui.view.dialog.effects.FadeIn;
import com.grgbanking.baselib.ui.view.dialog.effects.FlipH;
import com.grgbanking.baselib.ui.view.dialog.effects.FlipV;
import com.grgbanking.baselib.ui.view.dialog.effects.NewsPaper;
import com.grgbanking.baselib.ui.view.dialog.effects.SideFall;
import com.grgbanking.baselib.ui.view.dialog.effects.SlideLeft;
import com.grgbanking.baselib.ui.view.dialog.effects.SlideRight;
import com.grgbanking.baselib.ui.view.dialog.effects.SlideTop;

/*
 * Copyright 2014 litao
 * https://github.com/sd6352051/NiftyDialogEffects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum  Effectstype {

    Fadein(FadeIn.class),
    Slideleft(SlideLeft.class),
    Slidetop(SlideTop.class),
    SlideBottom(com.grgbanking.baselib.ui.view.dialog.effects.SlideBottom.class),
    Slideright(SlideRight.class),
    Fall(com.grgbanking.baselib.ui.view.dialog.effects.Fall.class),
    Newspager(NewsPaper.class),
    Fliph(FlipH.class),
    Flipv(FlipV.class),
    RotateBottom(com.grgbanking.baselib.ui.view.dialog.effects.RotateBottom.class),
    RotateLeft(com.grgbanking.baselib.ui.view.dialog.effects.RotateLeft.class),
    Slit(com.grgbanking.baselib.ui.view.dialog.effects.Slit.class),
    Shake(com.grgbanking.baselib.ui.view.dialog.effects.Shake.class),
    Sidefill(SideFall.class);
    private Class<? extends BaseEffects> effectsClazz;

    private Effectstype(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects=null;
        try {
            bEffects = effectsClazz.newInstance();
        } catch (ClassCastException e) {
            throw new Error("Can not init animatorClazz instance");
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            throw new Error("Can not init animatorClazz instance");
        }
        return bEffects;
    }
}

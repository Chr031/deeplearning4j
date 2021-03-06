/*-
 *
 *  * Copyright 2017 Skymind,Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 */
package org.deeplearning4j.nn.modelimport.keras.layers.convolutional;

import org.deeplearning4j.nn.conf.inputs.InputType;
import org.deeplearning4j.nn.conf.layers.Upsampling2D;
import org.deeplearning4j.nn.modelimport.keras.KerasLayer;
import org.deeplearning4j.nn.modelimport.keras.exceptions.InvalidKerasConfigurationException;
import org.deeplearning4j.nn.modelimport.keras.exceptions.UnsupportedKerasConfigurationException;

import java.util.Map;


/**
 * Keras Upsampling2D layer support
 *
 * @author Max Pumperla
 */
public class KerasUpsampling2D extends KerasLayer {

    /**
     * Constructor from parsed Keras layer configuration dictionary.
     *
     * @param layerConfig dictionary containing Keras layer configuration.
     * @throws InvalidKerasConfigurationException     Invalid Keras configuration exception
     * @throws UnsupportedKerasConfigurationException Unsupported Keras configuration exception
     */
    public KerasUpsampling2D(Map<String, Object> layerConfig)
            throws InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        this(layerConfig, true);
    }

    /**
     * Constructor from parsed Keras layer configuration dictionary.
     *
     * @param layerConfig           dictionary containing Keras layer configuration
     * @param enforceTrainingConfig whether to enforce training-related configuration options
     * @throws InvalidKerasConfigurationException     Invalid Keras configuration exception
     * @throws UnsupportedKerasConfigurationException Invalid Keras configuration exception
     */
    public KerasUpsampling2D(Map<String, Object> layerConfig, boolean enforceTrainingConfig)
            throws InvalidKerasConfigurationException, UnsupportedKerasConfigurationException {
        super(layerConfig, enforceTrainingConfig);

        int[] size = KerasConvolutionUtils.getUpsamplingSizeFromConfig(layerConfig, 2, conf);
        if (size[0] != size[1])
            throw new UnsupportedKerasConfigurationException("First and second size arguments have to be the same" +
                    "got: " + size[0] + " and " + size[1]);

        Upsampling2D.Builder builder = new Upsampling2D.Builder()
                .name(this.layerName)
                .dropOut(this.dropout)
                .size(size[0]);

        this.layer = builder.build();
        this.vertex = null;
    }

    /**
     * Get DL4J Upsampling2D layer.
     *
     * @return Upsampling2D layer
     */
    public Upsampling2D getUpsampling2DLayer() {
        return (Upsampling2D) this.layer;
    }

    /**
     * Get layer output type.
     *
     * @param inputType Array of InputTypes
     * @return output type as InputType
     * @throws InvalidKerasConfigurationException
     */
    @Override
    public InputType getOutputType(InputType... inputType) throws InvalidKerasConfigurationException {
        if (inputType.length > 1)
            throw new InvalidKerasConfigurationException(
                    "Keras Subsampling layer accepts only one input (received " + inputType.length + ")");
        return this.getUpsampling2DLayer().getOutputType(-1, inputType[0]);
    }

}

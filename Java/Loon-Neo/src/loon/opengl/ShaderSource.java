/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.opengl;

import loon.utils.StringKeyValue;

public abstract class ShaderSource {

	private final String _vertexShader;

	private final String _framentShader;

	public ShaderSource(String vertex, String frament) {
		this._vertexShader = vertex;
		this._framentShader = frament;
	}
	
	/**
	 * 于此动态设置着色器参数
	 */
	public abstract void setupShader(ShaderProgram program);

	public String vertexShader(){
		return _vertexShader;
	}
	
	public String fragmentShader(){
		return _framentShader;
	}
	
	@Override
	public String toString(){
		StringKeyValue builder = new StringKeyValue("ShaderSource");
		builder.newLine()
		.kv("vertexShader", _vertexShader)
		.newLine()
		.kv("framentShader", _framentShader)
		.newLine();
		return builder.toString();
	}

}
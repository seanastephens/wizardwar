/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/**
 * ... Heavily modified from the Oracle code--don't blame bugs in here on them!
 */
package view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;

import javax.swing.JFrame;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import server.Logger;
import messaging.DisconnectMessage;

public class ProgressManager extends JFrame implements PropertyChangeListener {

	private ProgressMonitor progressMonitor;
	private Task task;

	class Task extends SwingWorker<Void, Void> {
		@Override
		public Void doInBackground() {

			int progress = 5;
			boolean increasing = true;
			setProgress(progress);
			try {
				while (true) {
					Thread.sleep(50);
					if (increasing) {
						progress += 5;
						increasing = progress < 95;
					} else {
						progress -= 5;
						increasing = progress < 5;
					}
					setProgress(progress);
				}
			} catch (InterruptedException ie) {
			}
			return null;
		}

		@Override
		public void done() {

		}
	}

	private ObjectOutputStream output;
	
	public ProgressManager(ObjectOutputStream output) {
		this.output = output;
		progressMonitor = new ProgressMonitor(ProgressManager.this,
				"Connecting ...", null, 0, 100);
		progressMonitor.setProgress(0);
		task = new Task();
		task.addPropertyChangeListener(this);
		task.execute();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressMonitor.setProgress(progress);
			if (progressMonitor.isCanceled()) {
				try{
					output.writeObject(new DisconnectMessage(-1L, WizardGame.getPlayerID()));
					Logger.log(this, "Disconnecting from ProgressBar");
					Thread.sleep(500);
				} catch(Exception e){
					Logger.log(this, "Failed to disconnect");
				}
				System.exit(0);
			}
		}
	}

	public void kill() {
		task.cancel(true);
	}
}
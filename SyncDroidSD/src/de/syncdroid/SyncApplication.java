package de.syncdroid;

import java.util.List;

import roboguice.application.GuiceApplication;

import com.google.inject.Module;


public class SyncApplication extends GuiceApplication {
	protected void addApplicationModules(List<Module> modules) {
		modules.add(new SyncModule(this));
		
		
		
		
	}

}

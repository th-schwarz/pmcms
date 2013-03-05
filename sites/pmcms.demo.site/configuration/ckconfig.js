CKEDITOR.editorConfig = function( config )
{	
	config.toolbar = 'Default';
	config.toolbar_Default = [
		['Source','-','Undo','Redo','-','Cut','Copy','Paste','-','Bold','Italic','-','BulletedList','NumberedList','-','Link','Unlink','-','Image','-','Table'],
		'/',
		['Format','Styles','-','Maximize','-','About']
];
	
	config.shiftEnterMode = CKEDITOR.ENTER_BR;
	
	config.format_tags = 'p;h1;h2;h3';
	
	config.stylesSet = [
	   { name : 'Remark', element : 'p', attributes : { 'class' : 'remark' } }
];
};

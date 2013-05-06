%@@main
%Combine multiple stacks
%@@file:pathres@@file:pathim@@string:tranche@@value:slice@@
%@@none
function combine3D_im(varargin)

% Construction de matrice d'incr�ments (ligne,colonne) pour
% le parcours en spirale
pathres=varargin{1};
pathim=varargin{2};
tranche=(varargin{3});
slice=(varargin{4});
stri=strfind(tranche,':');
list=[str2num(tranche(1:stri(1)-1)):str2num(tranche(stri(1)+1:end))]
rmax=21;
cmax=21;
rmean=11;
cmean=11;
idx = [];
s = 1;
for n=1:rmax
   
    s = s*-1;
    
    temp = repmat([s 0],n,1);
    temp2 = repmat([0 -s],n,1);
    
    idx = [idx;temp(:) temp2(:)];
    
end

% Point de d�part
cc = cmean;
rc = rmean;

c = cc;
r = rc;
ind_pos=2;
% matrice d'indice
mat_ind=zeros(rmax,cmax);
mat_ind(r,c)=1;
for n=1:size(idx,1)
    
    c = c+idx(n,2);
    r = r+idx(n,1); 
    
    % Test pour savoir si le pixel courant n'est pas en dehors de l'image
    if c<1 || c>cmax || r<1 || r>rmax
        break
    end
    % Modification de la valeur du pixel courant
    mat_ind(r,c)=n+1;
end
liste=dir;
mat_reconstruction=zeros(size(mat_ind));
for tranche=3:max(size(liste))
    if ~isempty(regexpi(liste(tranche).name,'csv')) 
        res=strfind(liste(tranche).name,'_');
        num_tranche=str2num(liste(tranche).name(2:res(1)-1));
        num_im=str2num(liste(tranche).name(res(1)+2:res(2)-1));
        [r c]=find(mat_ind==num_im);
        mat_reconstruction(r,c,num_tranche)=1;
    end
end
non_gfp=[];
gfp=[];
zoverlap=my_Zcombine(pathim,pathres);
maxT1=0;
for nb_demande=1:max(size(list))%nargin
   [pix_total pix_total_gfp]=my_combine2D_cluster(pathres,pathim,list(nb_demande),[rmean, cmean],mat_reconstruction(:,:,list(nb_demande)));
   if nb_demande>1
      pix_total=pix_total((pix_total(:,3)>zoverlap),:); 
      pix_total(:,3)=(pix_total(:,3)-zoverlap)+(maxT1*(nb_demande-1));
      pix_total_gfp=pix_total_gfp((pix_total_gfp(:,3)>zoverlap),:); 
      pix_total_gfp(:,3)=(pix_total_gfp(:,3)-zoverlap)+(maxT1*(nb_demande-1));
   else
      maxT1=max([pix_total(:,3);pix_total_gfp(:,3)]);
   end
   non_gfp=[non_gfp; pix_total];
   gfp=[gfp; pix_total_gfp];
end

%% recalage (pt 1,1,1)
minval=min([non_gfp;gfp]);
non_gfp=non_gfp+(minval+1);
gfp=gfp+(minval+1);

end